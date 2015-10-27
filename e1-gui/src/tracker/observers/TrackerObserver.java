package tracker.observers;

import java.util.Observer;

import tracker.subsys.TrackerSubsystem;
import tracker.subsys.cfts.FaultToleranceSys;
import tracker.subsys.db.DBFaultToleranceSys;
import tracker.subsys.election.MasterElectionSys;


/**
 * Controller that is accessed by the view.
 * @author Irene
 * @author Jesus
 */
public abstract class TrackerObserver {
	
	private TrackerSubsystem instance;
	
	public TrackerObserver(Class<? extends TrackerSubsystem> classType) {
		this.instance = getInstance(classType);
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
