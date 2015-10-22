package tracker.controller;

import java.util.Observer;

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
	
	public void addObserver(Observer ov, Class<? extends TrackerSubsystem> re) {
		if (re.equals(MasterElectionSys.class)){
			melectionSys.addObserver(ov);
		} else {
			if (re.equals(DBFaultToleranceSys.class)) {
				dbSys.addObserver(ov);
			} else {
				if (re.equals(FaultToleranceSys.class)) {
					ackSys.addObserver(ov);
				}
			}
		}
		
	}
	
	public void rmObserver(Observer ov, Class<? extends TrackerSubsystem> re) {
		if (re.equals(MasterElectionSys.class)){
			melectionSys.removeObserver(ov);
		} else {
			if (re.equals(DBFaultToleranceSys.class)) {
				dbSys.removeObserver(ov);
			} else {
				if (re.equals(FaultToleranceSys.class)) {
					ackSys.removeObserver(ov);
				}
			}
		}
	}
}
