package tracker.controller;

import tracker.db.DBFaultToleranceSys;

public class DBFaultToleranceController  extends TrackerController {
	
	public DBFaultToleranceController() {
		super(DBFaultToleranceSys.class);
	}

}
