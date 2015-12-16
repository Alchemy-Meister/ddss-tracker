package tracker.subsys.cfts;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import bitTorrent.tracker.protocol.udp.messages.custom.LongLong;
import bitTorrent.tracker.protocol.udp.messages.custom.ka.KeepAliveM;
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
	private Timer timer;
	private static final Topic subscription = Topic.KA;
	private UUID uuid = UUID.randomUUID();
		
	private FaultToleranceSys() {
		super();
		masterElection = MasterElectionSys.getInstance();
		ipidTable = IpIdTable.getInstance();
		timer = new Timer();
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
		TrackerSubsystem.networker.subscribe(FaultToleranceSys.subscription, this);	
		timer.schedule(new KATimerTask(TrackerSubsystem.networker, this.uuid), 0);
		
		checkOfflineMembers();
		// check if the current master is ok
		checkMaster();
		
	}
	
	public void stop() {
		//TODO Unsubscribre to networker
		timer.cancel();
	}
	
	private void checkOfflineMembers() {
		List<TrackerMember> offlineMembers = ipidTable.getFallenMembers();
		for (TrackerMember tm : offlineMembers) {
			ipidTable.remove(tm.getIp());
			// notify observers
			// this.notifyObservers(param);
		}
	}
	
	private void checkMaster() {
		// check if the master is down (null on ip-id table)
		if (ipidTable.getMasterID() == null)
			masterElection.startMasterElection();
		// if the master's id is not the lowest, start a election process
		if (ipidTable.getMasterID().compareTo(
				ipidTable.getMemberLowestId().getId()) != 0)
			masterElection.startMasterElection();
	}
	
	@Override
	public void receive(Topic topic, Bundle bundle) {
		if (topic == FaultToleranceSys.subscription) {
			// TODO update ip id table
			this.ipidTable.set(bundle.getIP(), 
					((KeepAliveM)bundle.getMessage()).getId());
			// TODO notifyObservers
			this.notifyObservers(bundle);
		}
	}
	

	private class KATimerTask extends TimerTask {

		private Networker networker;
		private UUID uuid;
		
		public KATimerTask(Networker networker, UUID uuid) {
			this.networker = networker;
			this.uuid = uuid;
		}
		
		@Override
		public void run() {
			networker.publish(Topic.KA, new KeepAliveM(new LongLong(
					uuid.getMostSignificantBits(),
					uuid.getLeastSignificantBits())));
			timer.schedule(new KATimerTask(this.networker, uuid), 2000);
		}
		
	}

}
