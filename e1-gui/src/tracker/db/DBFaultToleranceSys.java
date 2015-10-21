package tracker.db;

/** The db replication is handled in this class.
 * @author Irene
 * @author Jesus
 */
public class DBFaultToleranceSys implements Runnable {

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
