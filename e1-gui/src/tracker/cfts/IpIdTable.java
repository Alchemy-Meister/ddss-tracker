package tracker.cfts;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import tracker.controller.TrackerSubsystem;
import tracker.db.model.TrackerMember;

/**
 * Encapsulates the methods required to check wich instances are up.
 * @author Irene
 * @author Jesus
 */
public class IpIdTable {
	
	// set a lock on this variable
	private BigInteger masterId = null;
	private ConcurrentHashMap<String, BigInteger> ipid = null;
	private ConcurrentHashMap<String, Long> ipTime = null;
	private static IpIdTable instance;
	
	// To propagate the changes to the view
	private TrackerSubsystem faultTolerance = null;
	
	private IpIdTable(FaultToleranceSys faultTolerance){
		this.ipid = new ConcurrentHashMap<String, BigInteger>();
		this.ipTime = new ConcurrentHashMap<String, Long>();
		this.faultTolerance = faultTolerance;
	}
	
	/**
	 * Explicit callback in singleton, a single Ip-id table.
	 * @return
	 */
	public static IpIdTable getInstance(FaultToleranceSys faultTolerance) {
		if (instance == null)
			instance = new IpIdTable(faultTolerance);
		return instance;
	}
	
	/** Implicit callback in singleton.
	 * @return
	 */
	public static IpIdTable getInstance() {
		if (instance == null)
			instance = new IpIdTable(FaultToleranceSys.getInstance());
		return instance;
	}
	
	public List<TrackerMember> getAll() {
		List<TrackerMember> ret = new LinkedList<TrackerMember>();
		// parse, or think a more efficient way of storing the members
		return ret;
	}
	
	public void set(String ip, BigInteger id) {
		ipid.put(ip, id);
		ipTime.put(ip, System.nanoTime());
		faultTolerance.notifyObservers(null);
	}
	
	public void remove(String ip) {
		ipid.remove(ip);
		ipTime.remove(ip);
		faultTolerance.notifyObservers(null);
	}
	
	public TrackerMember getMemberLowestId() {
		return null;
	}
	
	public BigInteger getMasterID() {
		// get lock, release for this variable
		return masterId;
	}
	
	public void electMaster(BigInteger newMasterID) {
		// get lock
		masterId = newMasterID;
		// release lock
	}
	
}
