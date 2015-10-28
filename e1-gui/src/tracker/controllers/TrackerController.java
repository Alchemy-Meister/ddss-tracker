package tracker.controllers;

import tracker.db.DBManager;

public class TrackerController {
	
	private DBManager manager = null;
	
	public TrackerController() {
		//TODO initialize database instance.
		manager = new DBManager();
		manager.connect();
	}
	
	public String[][] getMasterInfo() {
		// TODO return real data from DB.
		return new String[][]{{"0", "36.53.128.121", "5432", "8976",
			"2015-10-11T 10:45:32"}};
	}
	
	public String[][] getSlaveListInfo() {
		// TODO return real data from DB.
		return new String[][] {
			{"1", "36.53.128.122", "5432", "8976", "2015-10-11T 10:45:30"},
			{"2", "36.53.128.123", "5432", "8976", "2015-10-11T 10:45:31"},
			{"3", "36.53.128.124", "5432", "8976", "2015-10-11T 10:45:32"},
			{"4", "36.53.128.125", "5432", "8976", "2015-10-11T 10:45:30"},
			{"5", "36.53.128.126", "5432", "8976", "2015-10-11T 10:45:30"},
			{"6", "36.53.128.127", "5432", "8976", "2015-10-11T 10:45:31"},
			{"7", "36.53.128.128", "5432", "8976", "2015-10-11T 10:45:32"},
			{"8", "36.53.128.129", "5432", "8976", "2015-10-11T 10:45:32"}		
		};
	}
	
	public String[] getClusterColumnNames() {
		// TODO get the column names from database.
		return new String[]{"ID", "IP", "Cluster port", "Peer port",
				"Latest Keepalive"};
	}
}
