package tracker.subsys.cfts;

import java.util.List;

import tracker.db.model.TrackerMember;
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
		
	private FaultToleranceSys() {
		super();
		masterElection = MasterElectionSys.getInstance();
		ipidTable = IpIdTable.getInstance();
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
		
		// update table with incomming messages and notify observer
		// ipidTable.set(ip, id);
		// this.notifyObservers(param);
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
	

}
