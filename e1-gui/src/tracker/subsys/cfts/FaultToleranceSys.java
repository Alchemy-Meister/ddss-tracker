package tracker.subsys.cfts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import bitTorrent.tracker.protocol.udp.messages.custom.LongLong;
import bitTorrent.tracker.protocol.udp.messages.custom.hi.HelloM;
import bitTorrent.tracker.protocol.udp.messages.custom.ka.KeepAliveM;
import tracker.Const;
import tracker.db.model.TrackerMember;
import tracker.networking.Bundle;
import tracker.networking.Networker;
import tracker.networking.Topic;
import tracker.subsys.TrackerSubsystem;
import tracker.subsys.election.MasterElectionSys;

/** This component is in charge of sending/receiving KA messages from
 * the tracker members. It must update the IP-ID table.
 * @author Irene
 * @author Jesus
 */ 
public class FaultToleranceSys extends TrackerSubsystem implements Runnable {

	private static FaultToleranceSys instance = null;
	private IpIdTable ipidTable = null;
	private MasterElectionSys masterElection = null;
	private Timer timerKA, timerHI;

	private FaultToleranceSys() {
		super();
		masterElection = MasterElectionSys.getInstance();
		ipidTable = IpIdTable.getInstance();
		timerKA = new Timer();
		timerHI = new Timer();
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
		// 2- We also start sending HI messages
		timerHI.schedule(new HITimerTask(TrackerSubsystem.networker, 5),
				myRandom.nextLong());
		// 3.1 - if no KA messages are listened from other instances in 3 secs
		//       we elect ourselves as master
		// 3.1.1 stop HI messages
		// 3.1.2 update KA id
		
		checkOfflineMembers();
		// check if the current master is ok
		checkMaster();

	}

	public void stop() {
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
		} else {
			if (topic == Topic.HI) {
				
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
