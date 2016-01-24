package tracker.subsys.cfts;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.commons.codec.digest.DigestUtils;

import bitTorrent.tracker.protocol.udp.messages.custom.LongLong;
import bitTorrent.tracker.protocol.udp.messages.custom.SHA1;
import bitTorrent.tracker.protocol.udp.messages.custom.hi.Contents;
import bitTorrent.tracker.protocol.udp.messages.custom.hi.HelloBaseM;
import bitTorrent.tracker.protocol.udp.messages.custom.hi.HelloCloseM;
import bitTorrent.tracker.protocol.udp.messages.custom.hi.HelloM;
import bitTorrent.tracker.protocol.udp.messages.custom.hi.HelloResponseM;
import bitTorrent.tracker.protocol.udp.messages.custom.ka.KeepAliveM;
import sun.security.provider.SecureRandom;
import tracker.Const;
import tracker.db.DBManager;
import tracker.db.model.TrackerMember;
import tracker.networking.Bundle;
import tracker.networking.Networker;
import tracker.networking.Topic;
import tracker.subsys.PeerIdAssigner;
import tracker.subsys.TrackerSubsystem;
import tracker.subsys.election.MasterElectionSys;

/** This component is in charge of sending/receiving KA messages from
 * the tracker members. It must update the IP-ID table.
 * @author Irene
 * @author Jesus
 */ 
public class FaultToleranceSys extends TrackerSubsystem implements Runnable {

	private static FaultToleranceSys instance = null;
	private static Boolean running = false;
	private IpIdTable ipidTable = null;
	private MasterElectionSys masterElection = null;
	private Timer timerKA, timerHI;

	private DBManager manager;

	private long waitFromToElectMyself = -1;
	private boolean waitingForMaster = true;
	private boolean updateMaster = false;
	private List<Long> myHiConnectionIds = null;


	private FaultToleranceSys() {
		super();
		masterElection = MasterElectionSys.getInstance();
		ipidTable = IpIdTable.getInstance();
		timerKA = new Timer();
		timerHI = new Timer();
		manager = DBManager.getInstance();
		try {
			manager.createTables();
		} catch (SQLException e) {
			e.printStackTrace(System.err);
		}
		myHiConnectionIds = new ArrayList<Long>();
		if (Const.PRINTF_FTS) {
			System.out.println(" [FTS] Up and running.");
		}
	}

	/** 
	 * Singleton pattern, we need to ensure that there is just one.
	 * (we need a single ACK-sending-thread)
	 * @return
	 */
	public static FaultToleranceSys getInstance() {
		if (instance == null)
			instance = new FaultToleranceSys();
		return instance;
	}

	public boolean amIMaster() {
		return ipidTable.amIMaster();
	}


	@Override
	/**
	 * Sends ACK messages every second and updates the IP-ID table with
	 * the incomming ACK messages.
	 */
	public void run() {
		
		TrackerSubsystem.networker.subscribe(Topic.KA, this);
		TrackerSubsystem.networker.subscribe(Topic.HI, this);
		// 1- We start sending KAs with an unasigned id
		timerKA.schedule(new KATimerTask(TrackerSubsystem.networker,
				Const.UNASIGNED_ID), 0);
		waitFromToElectMyself = System.currentTimeMillis();
		waitingForMaster = true;
		// 2- We also start sending HI messages
		timerHI.schedule(new HITimerTask(TrackerSubsystem.networker, 
				myHiConnectionIds), 0);

		/*
		while(running) {
			//checkOfflineMembers();
			// check if the current master is ok
			//checkMaster();
		}
		*/

	}

	public void stop() {
		synchronized (running) {
			running = false;
		}
		//TODO Unsubscribre to networker
		myHiConnectionIds.clear();
		timerKA.cancel();
		timerKA = new Timer();
		timerHI.cancel();
		timerHI = new Timer();
	}

	@Override
	public void receive(Topic topic, Bundle bundle) {
		if (topic == Topic.KA) {
			if (Const.PRINTF_FTS)
				System.out.println("\n [FTS] KA:" + bundle);
			// update ip id table
			KeepAliveM mess = (KeepAliveM) bundle.getMessage();
			this.ipidTable.set(bundle.getIP(), mess.getId());
			// Notify observers about the latest KA
			List<String[]> slaveInfo = ipidTable.getSlaveInfo(
					bundle.getPort());
			List<String[]> masterInfo = new ArrayList<String[]>(); 
			String[] tempInfo = ipidTable.getMasterInfo(bundle.getPort());
			masterInfo.add(tempInfo);
			Map<String, List<String[]>> info = new HashMap<String,
					List<String[]>>();
			info.put("master", masterInfo);
			info.put("slaves", slaveInfo);
			this.notifyObservers(info);
			// Check if we where waiting to assign ourselves as master
			if (waitingForMaster) {
				if (Const.PRINTF_FTS) {
					System.out.println(" [FTS] I've been waiting for master: " +
							(System.currentTimeMillis()
									- waitFromToElectMyself) + "ms");
				}
				// check time limit
				if (System.currentTimeMillis() - waitFromToElectMyself
						>= Const.WAIT_BEFORE_IAM_MASTER)
				{
					if (Const.PRINTF_FTS) {
						System.out.println(" [FTS] Timeout waiting master.");
					}
					waitingForMaster = false;
					// I am the new master
					LongLong master = PeerIdAssigner.assignId();
					timerKA.cancel();
					timerHI.cancel();
					timerKA = new Timer();
					timerHI = new Timer();
					timerKA.schedule(new KATimerTask(TrackerSubsystem.networker,
							master), 0);
					ipidTable.setMyId(bundle.getIP(), master);
					ipidTable.electMaster(master);
					if (Const.PRINTF_FTS)
						System.out.println(" [FTS] I've elected myself as master");
				}
			}
		} else {
			if (topic == Topic.HI) {
				// NOTE: HI messages are ignored during ME process as said
				// in the documentation
				if (! masterElection.isInProgress()) {
					HelloBaseM hellobase = (HelloBaseM) bundle.getMessage();
					switch (hellobase.getSubtype()) {
					case HelloM.HI_INI:
						if (Const.PRINTF_FTS)
							System.out.println("\n [FTS] HI_INI:" + hellobase);
						// Check that we hadn't sent that hi
						boolean hiIamMaster = amIMaster();
						if (hiIamMaster && !myHiConnectionIds.contains(
								hellobase.getConnection_id()))
						{
							// Return everything on the CONTENTS table
							manager.connect();
							try {
								List<Contents> table = manager.getAllContents();
								// NOTE: when the contents table is empty
								// the HelloResponse Messages may be interpreted
								// as HelloClose Messages (see reference)
								// Thereby we are sending a triplet with
								// info_hash -> rubbish, host -> -1, port -> -1
								if (table.isEmpty()) {
									byte[] bytes = new byte[20];
									new SecureRandom().engineNextBytes(bytes);
									table.add(new Contents(new SHA1(bytes),
											-1, (short)-1));
								}
								String alltohash = "";
								for (Contents c : table)
									alltohash += c.prepareForHash();
								
								HelloM hellom = (HelloM) hellobase;
								byte[] hashBytes = DigestUtils.sha1(alltohash);
								if (Const.PRINTF_FTS) {
									System.out.println(" [FTS] Giving info"
											+ " and assigning id to slave.");
								}
								networker.publish(Topic.HI,new HelloResponseM(
										hellom.getConnection_id(),
										PeerIdAssigner.assignId(),
										new SHA1(hashBytes), table));
							} catch (Exception e) {
								e.printStackTrace(System.err);
							} finally {
									manager.disconnect();
							}
						} else {
							if (Const.PRINTF_FTS && hiIamMaster) {
								System.out.println(" [FTS] I'm ignoring"
											+ " a HI message that I sent"
											+ " before (now I AM master).");
							}
						}
							
						break;
					case HelloResponseM.HI_RES:
						if (!amIMaster()) {
							HelloResponseM responseM = (HelloResponseM) hellobase;
							// Check that we haven't already processed that hi
							if (myHiConnectionIds.contains(
									hellobase.getConnection_id())) {
								myHiConnectionIds.remove(
										hellobase.getConnection_id());
								waitingForMaster = false;
								// we are assigned an id by the master
								// and sent db info
								timerKA.cancel();
								timerHI.cancel();
								timerKA = new Timer();
								timerHI = new Timer();
								LongLong myID = responseM.getAssigned_id();
								ipidTable.setMyId(bundle.getIP(), myID);
								updateMaster = true;
								timerKA.schedule(
										new KATimerTask(
												TrackerSubsystem.networker,
												myID), 0);
								if (Const.PRINTF_FTS) {
									System.out.println(" [FTS] Master has " +
											"assigne me this id: "
											+ myID.toString());
								}
								// Save contents, remember that when host -> -1
								// and port -> -1 we don't have to save that
								manager.connect();
								for (Contents cont : responseM.getTriplets()) {
									if (cont.getHost() != -1 
											&& cont.getPort() != -1)
									{
										try{
										manager.insertContents(
												cont.getInfo_hash().toString(),
												Integer.toString(cont.getHost()),
												cont.getHost());
										} catch (Exception e) {
											e.printStackTrace(System.err);
										}
									}
								}
								manager.disconnect();
								networker.publish(Topic.HI,
										new HelloCloseM(responseM.getConnection_id(),
												myID,
												responseM.getContents_sha()));
							}
						}
						break;
					case HelloCloseM.HI_CLOSE:
						if (amIMaster()) {
							
						}
						break;
					default:
						break;
					}
				} else {
					if (Const.PRINTF_FTS)
						System.out.println("\n [FTS] HI message ignored," +
								" we are at ME process.");
				}
			}
		}
		// Finally check if someone is out
		ipidTable.checkFallenMembers();
		// If master has sent us a HIResponse we wait until a KA
		// to know the master ID and set up in our table who is master
		if (updateMaster) {
			boolean updated = ipidTable.updateMaster();
			updateMaster = !updated;
		}
		// Check if master is down
		if (!waitingForMaster) {
			if (ipidTable.isMasterFallen() && !masterElection.isInProgress()) {
				if (Const.PRINTF_FTS)
					System.out.println(" [FTS] MASTER DOWN!!! ABANDON SHIP!!");
				masterElection.startMasterElection();
			}
		}
		// Check if I am wrongly master
		if (amIMaster()) {
			LongLong mid = ipidTable.getMasterID();
			TrackerMember lowestMem = ipidTable.getMemberLowestId();
			if (mid != null && lowestMem != null) {
				if (!lowestMem.getId().toString().equals(mid)) {
					ipidTable.electMaster(ipidTable.getMemberLowestId().getId());
					updateMaster = false;
				}
			}
		}
	}


	private class KATimerTask extends TimerTask {

		private Networker networker;
		private LongLong uuid;

		public KATimerTask(Networker networker, LongLong uuid) {
			this.networker = networker;
			this.uuid = uuid;
		}

		@Override
		public void run() {
			networker.publish(Topic.KA, new KeepAliveM(uuid));
			timerKA.schedule(new KATimerTask(this.networker, uuid),
					Const.KA_EVERY);
		}

	}

	private class HITimerTask extends TimerTask {

		private Networker networker;
		private List<Long> hiConnectIds;
		private Random random;

		public HITimerTask(Networker networker, List<Long> hiConnectIds) {
			random = new Random(System.currentTimeMillis());
			this.networker = networker;
			this.hiConnectIds = hiConnectIds;
		}

		@Override
		public void run() {
			long newRandom = random.nextLong();
			hiConnectIds.add(newRandom);
			networker.publish(Topic.HI, new HelloM(newRandom));
			timerHI.schedule(new HITimerTask(this.networker, hiConnectIds),
					Const.HI_EVERY);
		}

	}

}
