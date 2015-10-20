package tracker;

import tracker.cfts.FaultToleranceSys;
import tracker.db.DBFaultToleranceSys;
import tracker.election.MasterElectionSys;

/**
 * Controller that is accessed by the view.
 * @author Irene
 * @author Jesus
 */
public class TrackerController {

	private FaultToleranceSys ackSys;
	private DBFaultToleranceSys dbSys;
	private MasterElectionSys melectionSys;
	
	public TrackerController() {
		
	}
	
}
