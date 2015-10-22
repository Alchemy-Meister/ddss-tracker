package tracker.db;

import tracker.controller.TrackerSubsystem;

/** The db replication is handled in this class.
 * @author Irene
 * @author Jesus
 */
public class DBFaultToleranceSys extends TrackerSubsystem implements Runnable {

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

}
