package tracker.election;

import tracker.cfts.IpIdTable;

/**
 * @author Irene
 * @author Jesus
 */
public class MasterElectionSys {
	
	private IpIdTable ipidTable = null;
	
	public MasterElectionSys() {
		ipidTable = IpIdTable.getInstance();
	}
}
