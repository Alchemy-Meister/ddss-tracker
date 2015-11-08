package tracker.subsys.db;

import tracker.networking.Topic;
import tracker.subsys.TrackerSubsystem;

/** The db replication is handled in this class.
 * @author Irene
 * @author Jesus
 */
public class DBFaultToleranceSys extends TrackerSubsystem implements Runnable {

	private static DBFaultToleranceSys instance = null;
	
	
	private DBFaultToleranceSys() {
		super();
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
	public void receive(Topic topic, String param) {
		// TODO Auto-generated method stub
		
	}


}
