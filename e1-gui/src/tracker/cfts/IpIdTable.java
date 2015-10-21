package tracker.cfts;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


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
	private static IpIdTable instance;
	
	private IpIdTable(){
		ipid = new ConcurrentHashMap<String, BigInteger>();
	}
	
	/**
	 * Singleton, a single Ip-id table.
	 * @return
	 */
	public static IpIdTable getInstance() {
		if (instance == null)
			instance = new IpIdTable();
		return instance;
	}
	
	public List<TrackerMember> getAll() {
		List<TrackerMember> ret = new LinkedList<TrackerMember>();
		// parse, or think a more efficient way of storing the members
		return ret;
	}
	
	public void set(String ip, BigInteger id) {
		// set value
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
