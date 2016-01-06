package tracker.subsys.cfts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;

import bitTorrent.tracker.protocol.udp.messages.custom.LongLong;
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


	private FaultToleranceSys() {
		super();
		masterElection = MasterElectionSys.getInstance();
		ipidTable = IpIdTable.getInstance();
		timerKA = new Timer();
		timerHI = new Timer();
		manager = DBManager.getInstance();
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
		Random myRandom = new Random(System.currentTimeMillis());
		TrackerSubsystem.networker.subscribe(Topic.KA, this);
		// 1- We start sending KAs with an unasigned id
		timerKA.schedule(new KATimerTask(TrackerSubsystem.networker,
				Const.UNASIGNED_ID), 0);
		waitFromToElectMyself = System.currentTimeMillis();
		waitingForMaster = true;
		// 2- We also start sending HI messages
		//TODO CHECK THIS FIX.
		/*timerHI.schedule(new HITimerTask(TrackerSubsystem.networker, 5),
				myRandom.nextLong());*/
		timerHI.schedule(new HITimerTask(TrackerSubsystem.networker, 
				myRandom.nextLong()), 0);

		while(running) {
			//checkOfflineMembers();
			// check if the current master is ok
			//checkMaster();
		}

	}

	public void stop() {
		synchronized (running) {
			running = false;
		}
		//TODO Unsubscribre to networker
		timerKA.cancel();
		timerHI.cancel();
	}

	private void checkOfflineMembers() {
		List<TrackerMember> offlineMembers = ipidTable.getFallenMembers();
		for (TrackerMember tm : offlineMembers) {
			Map<String, TrackerMember> order = new HashMap<String, TrackerMember>();
			order.put(Const.DELETE_ROW, tm);
			this.notifyObservers(order);
		}
	}

	private void checkMaster() {
		// check if the master is down (null on ip-id table)
		if (ipidTable.getMasterID() == null)
			masterElection.startMasterElection();
		else {
			// if the master's id is not the lowest, start a election process
			if (ipidTable.getMasterID().compareTo(
					ipidTable.getMemberLowestId().getId()) != 0)
				masterElection.startMasterElection();
		}

	}

	@Override
	public void receive(Topic topic, Bundle bundle) {
		if (topic == Topic.KA) {
			// update ip id table
			KeepAliveM mess = (KeepAliveM) bundle.getMessage();
			this.ipidTable.set(bundle.getIP(), mess.getId());
			// notifyObservers if the id != unasigned
			if (!mess.getId().equals(Const.UNASIGNED_ID)) {
				Map<String, Bundle> order = new HashMap<String, Bundle>();
				order.put(Const.ADD_ROW, bundle);
				this.notifyObservers(order);
			}
			
			// Check if we where waiting to assign ourselves as master
			if (waitingForMaster) {
				// check time limit
				if (System.currentTimeMillis() - waitFromToElectMyself
						>= Const.WAIT_BEFORE_IAM_MASTER)
				{
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
						if (amIMaster()) {
							// Return everithing on the CONTENTS table
							manager.connect();
							try {
								List<Contents> table = manager.getAllContents();
								// NOTE: when the contents table is empty
								// the HelloResponse Messages may be interpreted
								// as HelloClose Messages (see reference)
								// Thereby we are sending a triplet with
								// info_hash -> rubbish, host -> -1, port -> -1
								if (table.isEmpty()) {
									byte[] bytes = new byte[16];
									new SecureRandom().engineNextBytes(bytes);
									table.add(new Contents(new LongLong(bytes),
											-1, (short)-1));
								}
								String alltohash = "";
								for (Contents c : table)
									alltohash += c.prepareForHash();
								
								HelloM hellom = (HelloM) hellobase;
								byte[] hashBytes = DigestUtils.sha1(alltohash);
								networker.publish(Topic.HI,
										new HelloResponseM(
												hellom.getConnection_id(),
												PeerIdAssigner.assignId(),
												new LongLong(hashBytes),
												table));
							} catch (Exception e) {
							} finally {
								manager.disconnect();
							}
						}
						break;
					case HelloResponseM.HI_RES:
						if (!amIMaster()) {
							// we are assigned an id by the master and sent db info
							timerKA.cancel();
							timerHI.cancel();
							timerKA = new Timer();
							timerHI = new Timer();
							HelloResponseM responseM = (HelloResponseM) hellobase;
							LongLong myID = responseM.getAssigned_id();
							// TODO - check what is my ip
							ipidTable.setMyId(bundle.getIP(), myID);
							timerKA.schedule(new KATimerTask(TrackerSubsystem.networker,
									myID), 0);
							// Save contents, remember that when host -> -1
							// and port -> -1 we don't have to save that
							for (Contents cont : responseM.getTriplets()) {
								if (cont.getHost() != -1 && cont.getPort() != -1) {
									manager.connect();
									// TODO insert contents
									manager.disconnect();
								}
							}
						}
						break;
					case HelloCloseM.HI_CLOSE:
						// TODO tomorrow
						break;
					default:
						break;
					}
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
		private long connection_id;

		public HITimerTask(Networker networker, long connection_id) {
			this.networker = networker;
			this.connection_id = connection_id;
		}

		@Override
		public void run() {
			networker.publish(Topic.HI, new HelloM(connection_id));
			timerHI.schedule(new HITimerTask(this.networker, connection_id),
					Const.HI_EVERY);
		}

	}

}
