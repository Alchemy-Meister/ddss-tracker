package bitTorrent.tracker.protocol.udp.messages.custom.hi;

import bitTorrent.tracker.protocol.udp.messages.custom.LongLong;

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
public class HelloResponseM extends HelloBaseM {

	private LongLong assigned_id;
	private LongLong contents_sha;
	private LongLong info_hash;
	private int host;
	private short port;
	
	public HelloResponseM(long connection_id, LongLong assigned_id,
			LongLong contents_sha, LongLong info_hash, int host,
			short port)
	{
		super(connection_id);
		this.assigned_id = assigned_id;
		this.contents_sha = contents_sha;
		this.info_hash = info_hash;
		this.host = host;
		this.port = port;
	}

	public LongLong getAssigned_id() {
		return assigned_id;
	}

	public void setAssigned_id(LongLong assigned_id) {
		this.assigned_id = assigned_id;
	}

	public LongLong getContents_sha() {
		return contents_sha;
	}

	public void setContents_sha(LongLong contents_sha) {
		this.contents_sha = contents_sha;
	}

	public LongLong getInfo_hash() {
		return info_hash;
	}

	public void setInfo_hash(LongLong info_hash) {
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

	@Override
	public byte[] getBytes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

}
