package tracker.subsys.cfts;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import tracker.db.model.TrackerMember;
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
		
	private FaultToleranceSys() {
		super();
		masterElection = MasterElectionSys.getInstance();
		ipidTable = IpIdTable.getInstance();
		timer = new Timer();
		// ini networker
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
		networker.subscribe(FaultToleranceSys.subscription, this);	
		timer.schedule(new KATimerTask(this.networker), 0);
		
		checkOfflineMembers();
		// check if the current master is ok
		checkMaster();
		
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
	public void receive(Topic topic, String param) {
		if (topic == FaultToleranceSys.subscription) {
			// TODO update ip id table
			// TODO notifyObservers
		}
	}
	

	private class KATimerTask extends TimerTask {

		private Networker networker;
		
		public KATimerTask(Networker networker) {
			this.networker = networker;
		}
		
		@Override
		public void run() {
			//networker.send("KA"); // TODO change to actual message
			timer.schedule(new KATimerTask(this.networker), 2000);
		}
		
	}

}
