package tracker.db.model;

import bitTorrent.tracker.protocol.udp.messages.custom.LongLong;


/** Represents a member of the cluster.
 * @author Irene
 * @author Jesus
 */
public class TrackerMember {

	private String ip = null;
	private LongLong id = null;

	public TrackerMember(String ip, LongLong id) {
		this.ip = ip;
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public LongLong getId() {
		return id;
	}

	public void setId(LongLong id) {
		this.id = id;
	}
}
