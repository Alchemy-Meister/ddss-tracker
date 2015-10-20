package tracker.db.model;

import sun.security.util.BigInt;

/** Represents a member of the swarm.
 * @author Irene
 * @author Jesus
 */
public class TrackerMember {
	// TODO: add database ORM
	
	private String ip = null;
	private Integer port = null;
	private BigInt id = null;
	
	public TrackerMember(String ip, Integer port) {
		this.ip = ip;
		this.port = port;
	}
	
	public TrackerMember(String ip, Integer port, BigInt id) {
		this.ip = ip;
		this.port = port;
		this.id = id;
	}
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public BigInt getId() {
		return id;
	}
	public void setId(BigInt id) {
		this.id = id;
	}

}
