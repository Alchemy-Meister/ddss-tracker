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
public abstract class TrackerController {
	
	private TrackerSubsystem instance;
	
	public TrackerController(Class<? extends TrackerSubsystem> classType) {
		this.instance = getInstance(classType);
		System.out.println(this.instance);
	}
	
	public void addObserver(Observer ov) {
		this.instance.addObserver(ov);
	}
	
	public void rmObserver(Observer ov) {
		this.instance.removeObserver(ov);
	}
	
	private TrackerSubsystem getInstance(Class<? extends TrackerSubsystem> clas) {
		if (clas.equals(DBFaultToleranceSys.class)) {
			return DBFaultToleranceSys.getInstance();
		} else {
			if (clas.equals(FaultToleranceSys.class)) {
				return FaultToleranceSys.getInstance();
			} else {
				return MasterElectionSys.getInstance();
			}
		}
	}

}
