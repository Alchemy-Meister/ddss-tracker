package tracker.cfts;

/** This component is in charge of sending/receiving KA messages from
 * the swarm members. It must update the IP-ID table.
 * @author Irene
 * @author Jesus
 */
public class FaultToleranceSys implements Runnable {

	private static FaultToleranceSys instance = null;
	private IpIdTable ipidTable = null;
	
	private FaultToleranceSys() {
		ipidTable = IpIdTable.getInstance();
	}
	
	/** 
	 * Singleton pattern, we need to ensure that there is just one.
	 * (we need a single ACK-sending-thread)
	 * @return
	 */
	public static FaultToleranceSys getInstance() {
		if (instance == null)
			instance = new FaultToleranceSys();
		return instance;
	}

	@Override
	/**
	 * Sends ACK messages every second and updates the IP-ID table
	 */
	public void run() {		
	}
	

}
