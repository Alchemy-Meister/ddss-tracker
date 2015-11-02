package bitTorrent.tracker.protocol.udp.messages.custom.hi;

import java.math.BigInteger;

/**
 * Offset  Size	 			Name   			Value
 * 0       32-bit integer	type   		    2 // hi message
 * 4       64-bit integer   connection_id   
 * --
 * 12	   128-bit integer  assigned_id
 * 28      128-bit integer  contents_sha
 * 44      128-bit integer  info_hash                         |
 * 60      32-bit integer   host							  | Repeat
 * 64      16-bit integer   port							  |
 * @author Irene
 * @author Jesus
 *
 */
public class HelloResponseM extends HelloM {

	private BigInteger assigned_id;
	private BigInteger contents_sha;
	private BigInteger info_hash;
	private int host;
	private short port;
	
	public HelloResponseM(long connection_id, BigInteger assigned_id,
			BigInteger contents_sha, BigInteger info_hash, int host,
			short port)
	{
		super(connection_id);
		this.assigned_id = assigned_id;
		this.contents_sha = contents_sha;
		this.info_hash = info_hash;
		this.host = host;
		this.port = port;
	}

	public BigInteger getAssigned_id() {
		return assigned_id;
	}

	public void setAssigned_id(BigInteger assigned_id) {
		this.assigned_id = assigned_id;
	}

	public BigInteger getContents_sha() {
		return contents_sha;
	}

	public void setContents_sha(BigInteger contents_sha) {
		this.contents_sha = contents_sha;
	}

	public BigInteger getInfo_hash() {
		return info_hash;
	}

	public void setInfo_hash(BigInteger info_hash) {
		this.info_hash = info_hash;
	}

	public int getHost() {
		return host;
	}

	public void setHost(int host) {
		this.host = host;
	}

	public short getPort() {
		return port;
	}

	public void setPort(short port) {
		this.port = port;
	}

}
