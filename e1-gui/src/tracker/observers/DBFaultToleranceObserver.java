package tracker.observers;

import tracker.subsys.db.DBFaultToleranceSys;

public class DBFaultToleranceObserver  extends TrackerObserver {
	
	public DBFaultToleranceObserver() {
		super(DBFaultToleranceSys.class);
	}

}
