package tracker.subsys.cfts;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import bitTorrent.tracker.protocol.udp.messages.custom.LongLong;
import tracker.db.model.TrackerMember;

/**
 * Encapsulates the methods required to check wich instances are up.
 * @author Irene
 * @author Jesus
 */
public class IpIdTable {
	
	// set a lock on this variable
	private BigInteger masterId = null;
	private ConcurrentHashMap<String, LongLong> ipid = null;
	private ConcurrentHashMap<String, Long> ipTime = null;
	private static IpIdTable instance;
		
	private IpIdTable(){
		this.ipid = new ConcurrentHashMap<String, LongLong>();
		this.ipTime = new ConcurrentHashMap<String, Long>();
	}
	
	/**
	 * Explicit callback in singleton, a single Ip-id table.
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
	
	public void set(String ip, LongLong id) {
		ipid.put(ip, id);
		ipTime.put(ip, System.nanoTime());
	}
	
	public void remove(String ip) {
		ipid.remove(ip);
		ipTime.remove(ip);
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
	
	public List<TrackerMember> getFallenMembers() {
		return new ArrayList<TrackerMember>();
	}
	
}
