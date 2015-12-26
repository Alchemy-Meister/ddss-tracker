package tracker.subsys.cfts;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import bitTorrent.tracker.protocol.udp.messages.custom.LongLong;
import tracker.db.model.TrackerMember;

/**
 * Encapsulates the methods required to check wich instances are up.
 * @author Irene
 * @author Jesus
 */
public class IpIdTable {

	
	private final Lock masterIdLock = new ReentrantLock();
	private final Lock myIdLock = new ReentrantLock();
	private LongLong masterId = null, myId = null;
	// NOTE: take into account members with an UNASSIGNED ID = 0
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
	public synchronized static IpIdTable getInstance() {
			if (instance == null)
				instance = new IpIdTable();
		return instance;
	}

	public List<TrackerMember> getAll() {
		List<TrackerMember> ret = new ArrayList<TrackerMember>();
		Enumeration<String> en = ipid.keys();
		while (en.hasMoreElements()) {
			String key = en.nextElement();
			LongLong id = ipid.get(key);
			if (id != null)
				ret.add(new TrackerMember(key, id));
		}
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
	
	/** Set's the current trackers ip and id.
	 * @param ip
	 * @param id
	 */
	public void setMyId(String ip, LongLong id) {
		myIdLock.lock();
		this.set(ip, id);
		try {
			this.myId = id;
		} finally{
			myIdLock.unlock();
		}
	}
	
	public LongLong getMyId() {
		LongLong temp = null;
		myIdLock.lock();
		try {
			temp = this.myId;
		} finally {
			myIdLock.unlock();
		}
		return temp;
	}
	
	/** Returns the member with the lowest id. This id is the first
	 * TrackerMember to have and id of 1, or the one with the lowest id.
	 * IDs with 0 value are ignored (since 0 means an unasigned id).
	 * @return
	 */
	public TrackerMember getMemberLowestId() {
		Enumeration<String> en = ipid.keys();
		LongLong lowest = null;
		String key = null;
		boolean oneFound = false;
		while (en.hasMoreElements() && !oneFound) {
			key = en.nextElement();
			LongLong id = ipid.get(key);
			if (id != null && !id.equals(new LongLong("0"))) {
				if (id.equals(new LongLong("1"))) {
					oneFound = true;
					lowest = id;
				} else {
					if (id.compareTo(lowest) == -1)
						lowest = id;
				}
			}
		}
		return lowest == null ? null : new TrackerMember(key, lowest);
	}

	public LongLong getMasterID() {
		LongLong temp = null;
		masterIdLock.lock();
		try{
			temp = masterId;
		} finally {
			masterIdLock.unlock();
		}
		return temp;
	}

	/** Sets the given ID as the master's id.
	 * @param newMasterID
	 */
	public void electMaster(LongLong newMasterID) {
		masterIdLock.lock();
		try{
			masterId = newMasterID;
		} finally {
			masterIdLock.unlock();
		}
	}

	/** Returns the list of fallen members, a member is considered fallen
	 * if we do not know about it in two seconds ore more.
	 * This method removes the fallen members.
	 * @return
	 */
	public List<TrackerMember> getFallenMembers() {
		System.nanoTime();
		List<TrackerMember> ret = new ArrayList<TrackerMember>();
		Enumeration<String> en = ipTime.keys();
		while (en.hasMoreElements()) {
			String key = en.nextElement();
			if (System.nanoTime() - ipTime.get(key) >= (2 / 1000000000)) {
				LongLong id = ipid.remove(key);
				ipTime.remove(key);
				ret.add(new TrackerMember(key, id));
			}
		}
		return ret;
	}

}
