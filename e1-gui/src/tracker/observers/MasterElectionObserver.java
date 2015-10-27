package tracker.observers;

import tracker.subsys.election.MasterElectionSys;

public class MasterElectionObserver extends TrackerObserver {

	public MasterElectionObserver() {
		super(MasterElectionSys.class);
	}
	
}
