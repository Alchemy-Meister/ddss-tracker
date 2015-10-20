package tracker.cfts;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import sun.security.util.BigInt;
import tracker.db.model.TrackerMember;

/**
 * Encapsulates the methods required to check wich instances are up.
 * @author Irene
 * @author Jesus
 */
public class IpIdTable {
	
	private BigInt masterId = null;
	private ConcurrentHashMap<String, BigInt> ipid = null;
	private ConcurrentHashMap<BigInt, Integer> idport = null;
	private static IpIdTable instance;
	
	private IpIdTable(){
		ipid = new ConcurrentHashMap<String, BigInt>();
		idport = new ConcurrentHashMap<BigInt, Integer>();
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
		// Python, change to java
		// for ip in ipid.keyset():
		//		ret.append(TrackerMember(ip, ipid[ip], idport[ipid[ip]]))
		return ret;
	}
	
	public void set(String ip, Integer port, BigInt id) {
		
	}
	
	public TrackerMember getMemberLowestId() {
		return null;
	}
	
}
