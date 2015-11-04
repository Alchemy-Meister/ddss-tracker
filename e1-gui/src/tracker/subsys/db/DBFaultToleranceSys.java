package tracker.subsys.db;

import tracker.networking.InScheduler;
import tracker.networking.OutScheduler;
import tracker.subsys.TrackerSubsystem;

/** The db replication is handled in this class.
 * @author Irene
 * @author Jesus
 */
public class DBFaultToleranceSys extends TrackerSubsystem implements 
	InScheduler, OutScheduler, Runnable {

	private static DBFaultToleranceSys instance = null;
	
	
	private DBFaultToleranceSys() {
		
	}
	
	/** Singleton. Just one thread.
	 * @return
	 */
	public static DBFaultToleranceSys getInstance() {
		if (instance == null)
			instance = new DBFaultToleranceSys();
		return instance;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void send() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receive() {
		// TODO Auto-generated method stub
		
	}

}
