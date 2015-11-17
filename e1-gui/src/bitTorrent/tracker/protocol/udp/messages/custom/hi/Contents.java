package bitTorrent.tracker.protocol.udp.messages.custom.hi;

import java.nio.ByteBuffer;

import bitTorrent.tracker.protocol.udp.messages.custom.LongLong;

/**
 * Holds a triplet of (info_hash, host and port) used in HelloResponseM
 * messages.
 * @author Irene
 * @author Jesus 
 */
public class Contents {
	private LongLong info_hash; // TODO change to repeat
	private int host;
	private short port;
	
	public Contents(LongLong info_hash, int host, short port) {
		this.info_hash = info_hash;
		this.host = host;
		this.port = port;
	}
	
	public byte[] getBytes() {
		byte[] hashBytes = this.info_hash.getBytes();
		byte[] hostBytes = ByteBuffer.allocate(4).putInt(this.host).array();
		byte[] portBytes = ByteBuffer.allocate(2).putShort(this.port).array();
		byte[] ret = new byte[hashBytes.length + hostBytes.length
		                      + portBytes.length];
		System.arraycopy(hashBytes, 0, ret, 0, hashBytes.length);
		System.arraycopy(hostBytes, 0, ret, hashBytes.length, hostBytes.length);
		System.arraycopy(portBytes, 0, ret, 
				hashBytes.length + hostBytes.length,
				portBytes.length);
		return ret;
	}
	
	public int getSize() {
		return 16 + 4 + 2;
	}
}
