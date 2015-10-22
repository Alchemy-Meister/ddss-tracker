package tracker.db.model;

import java.math.BigInteger;


/** Represents a member of the cluster.
 * @author Irene
 * @author Jesus
 */
public class TrackerMember {
	// TODO: add database ORM
	
	private static final Integer defaultPort = 5432;
	
	private String ip = null;
	private Integer port = null;
	private BigInteger id = null;
	
	public TrackerMember(String ip, Integer port) {
		this.ip = ip;
		if (port == null)
			this.port = defaultPort;
	}
	
	public TrackerMember(String ip, Integer port, BigInteger id) {
		this.ip = ip;
		if (port == null)
			this.port = defaultPort;
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
	public BigInteger getId() {
		return id;
	}
	public void setId(BigInteger id) {
		this.id = id;
	}

}
