package tracker.subsys.election;

import tracker.subsys.TrackerSubsystem;
import tracker.subsys.cfts.IpIdTable;
import tracker.db.model.TrackerMember;

/** In charge of handling the master election system.
 * @author Irene
 * @author Jesus
 */
public class MasterElectionSys extends TrackerSubsystem implements Runnable {
	
	private static MasterElectionSys instance = null;
	private IpIdTable ipidTable = null;
	
	
	private MasterElectionSys() {
		ipidTable = IpIdTable.getInstance();
	}
	
	public static MasterElectionSys getInstance() {
		if (instance == null)
			instance = new MasterElectionSys();
		return instance;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Starts a new master election process.
	 */
	public void startMasterElection() {
		boolean consensus = false;
		TrackerMember myVote = ipidTable.getMemberLowestId();
		// whole process goes here
		// ...
		// TODO: wrong var (just to suppress warnings)
		// the master will be elected in the process
		TrackerMember newMaster = myVote; 
		// ...
		if (consensus)
			ipidTable.electMaster(newMaster.getId());
		else
			startMasterElection();
	}
}
