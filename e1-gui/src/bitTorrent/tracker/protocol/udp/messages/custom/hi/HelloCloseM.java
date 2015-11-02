package bitTorrent.tracker.protocol.udp.messages.custom.hi;

import java.math.BigInteger;

/**
 * Offset  Size	 			Name   			Value
 * 0       32-bit integer	type   		    2 // hi message
 * 4       64-bit integer   connection_id   
 * --
 * 12	   128-bit integer  assigned_id
 * 28      128-bit integer  contents_sha
 * @author Jesus
 *
 */
public class HelloCloseM extends HelloM {

	private BigInteger assigned_id;
	private BigInteger contents_sha;
	
	public HelloCloseM(long connection_id,BigInteger assigned_id,
			BigInteger contents_sha)
	{
		super(connection_id);
		this.assigned_id = assigned_id;
		this.contents_sha = contents_sha;
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

}
