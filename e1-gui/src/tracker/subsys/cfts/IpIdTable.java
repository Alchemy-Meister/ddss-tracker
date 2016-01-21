package tracker.subsys.cfts;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import bitTorrent.tracker.protocol.udp.messages.custom.LongLong;
import tracker.Const;
import tracker.db.model.TrackerMember;

/**
 * Encapsulates the methods required to check wich instances are up.
 * @author Irene
 * @author Jesus
 */
public class IpIdTable {
	private final Lock masterIdLock = new ReentrantLock();
	private final Lock myIdLock = new ReentrantLock();
	private String masterId = null, myId = null;
	// NOTE: take into account members with an UNASSIGNED ID = 0
	
	private ConcurrentHashMap<String, String> idIp = null;
	private ConcurrentHashMap<String, Long> idTime = null;
	
	private static IpIdTable instance;

	private IpIdTable(){
		this.idIp = new ConcurrentHashMap<String, String>();
		this.idTime = new ConcurrentHashMap<String, Long>();
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
		Enumeration<String> en = idIp.keys();
		while (en.hasMoreElements()) {
			String key = en.nextElement();
			String ip = idIp.get(key);
			if (ip != null)
				ret.add(new TrackerMember(ip, new LongLong(key)));
		}
		return ret;
	}

	public void set(String ip, LongLong id) {
		if (!id.equals(Const.UNASIGNED_ID)){   // do not track unassigned ids
			idIp.put(id.toString(), ip);
			idTime.put(id.toString(), System.currentTimeMillis());
		}
	}

	public void remove(LongLong id) {
		idIp.remove(id.toString());
		idTime.remove(id.toString());	
	}
	
	/** Set's the current trackers ip and id.
	 * @param ip
	 * @param id
	 */
	public void setMyId(String ip, LongLong id) {
		myIdLock.lock();
		this.set(ip, id);
		try {
			this.myId = id.toString();
		} finally{
			myIdLock.unlock();
		}
	}
	
	public LongLong getMyId() {
		String temp = null;
		myIdLock.lock();
		try {
			temp = this.myId;
		} finally {
			myIdLock.unlock();
		}
		return new LongLong(temp);
	}
	
	/** Returns the member with the lowest id. This id is the first
	 * TrackerMember to have and id of 1, or the one with the lowest id.
	 * IDs with 0 value are ignored (since 0 means an unasigned id).
	 * @return
	 */
	public TrackerMember getMemberLowestId() {
		if (idIp.size() == 0)
			return null;
		List<String> keys = Collections.list(idIp.keys());
		BigInteger min = null;
		for (String key : keys) {
			BigInteger temp = new BigInteger(key);
			if (min == null)
				min = temp;
			if (temp.compareTo(min) == -1)
				min = temp;
		}
		return min == null ? null : new TrackerMember(idIp.get(min.toString()),
				new LongLong(min.toString()));
	}
	
	/**
	 * Returns the highest id. Null if no ids are currently asigned. 
	 * Unassigned ids do not count.
	 * @return
	 */
	public LongLong getHighestId() {
		if (idIp.size() == 0)
			return null;
		List<String> keys = Collections.list(idIp.keys());
		BigInteger max = null;
		for (String key : keys) {
			BigInteger temp = new BigInteger(key);
			if (max == null)
				max = temp;
			if (temp.compareTo(max) == 1)
				max = temp;
		}
		return max == null ? null : new LongLong(max.toString());
	}

	public LongLong getMasterID() {
		String temp = null;
		masterIdLock.lock();
		try{
			temp = masterId;
		} finally {
			masterIdLock.unlock();
		}
		
		return temp == null ? null : new LongLong(temp);
	}

	/** Sets the given ID as the master's id.
	 * @param newMasterID
	 */
	public void electMaster(LongLong newMasterID) {
		masterIdLock.lock();
		try{
			masterId = newMasterID.toString();
		} finally {
			masterIdLock.unlock();
		}
	}
	
	public void setMasterFallen() {
		masterIdLock.lock();
		try {
			masterId = null;
		} finally {
			masterIdLock.unlock();
		}
	}
	
	public void checkFallenMembers() {
		List<String> members = Collections.list(idTime.keys());
		LongLong mid = getMasterID();
		String masterId = null;
		if (mid != null)
			masterId = mid.toString();
		for (String key : members) {
				long time = System.currentTimeMillis();
				if (time - idTime.get(key) > (2 * 1000)) {
					if (masterId != null && masterId.equals(key))
						setMasterFallen();
					idIp.remove(key);
					idTime.remove(key);
				}
		}
	}

	/** Returns the list of fallen members, a member is considered fallen
	 * if we do not know about it in two seconds ore more.
	 * This method removes the fallen members.
	 * @return
	 */
	public List<TrackerMember> getFallenMembers() {
		List<TrackerMember> ret = new ArrayList<TrackerMember>();
		List<String> members = Collections.list(idTime.keys());
		LongLong mid = getMasterID();
		String masterId = null;
		if (mid != null)
			masterId = mid.toString();
		for (String key : members) {
				long time = System.currentTimeMillis();
				if (time - idTime.get(key) > (2 * 1000)) {
					if (masterId != null && masterId.equals(key))
						setMasterFallen();
					String ip = idIp.remove(key);
					idTime.remove(key);
					ret.add(new TrackerMember(ip, new LongLong(key)));
				}
		}
		return ret;
	}
	
	public boolean amIMaster() {
		LongLong masterId = getMasterID();
		if (masterId == null)
			return false;
		LongLong myid = getMyId();
		if (myid == null || myid.toString().equals("0"))
			return false;
		return getMyId().toString().equals(masterId.toString());
	}
	
	/** Returns an array with the info of the slaves. The array holds
	 * the id, ip, cluster port and timestamp of the latest KA.
	 * @param defaultPort
	 * @return
	 */
	public List<String[]> getSlaveInfo(int defaultPort) {
		String port = new Integer(defaultPort).toString();
		List<String[]> slaves = new ArrayList<String[]>();
		LongLong tid = getMasterID();
		String masterId = null;
		if (tid != null)
			masterId = tid.toString();
		for (String key: Collections.list(idIp.keys())) {
			if (masterId != null && !masterId.equals(key)) {
				// id, ip, cluster port, latest KA
				if (Const.PRINTF_IPID) {
					System.out.println(" [IPID-T] Slave, id: "
							+ key + ", ip:" + idIp.get(key));
				}
				String[] temp = {key, idIp.get(key), port,
						idTime.get(key).toString()};
				slaves.add(temp);
			}
		}
		return slaves;
	}
	
	/** Returns an array with the info of the master. The array holds
	 * the id, ip, cluster port and timestamp of the latest KA.
	 * @param defaultPort
	 * @return
	 */
	public String[] getMasterInfo(int defaultPort) {
		String[] ret = null;
		LongLong master = getMasterID();
		if (master != null) {
			ret = new String[] {master.toString(), idIp.get(master.toString()),
					new Integer(defaultPort).toString(),
					idTime.get(master.toString()).toString()};
		}
		return ret;
	}

}
