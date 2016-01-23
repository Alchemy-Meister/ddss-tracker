package bitTorrent.tracker.protocol.udp.messages.custom.hi;

import java.nio.ByteBuffer;

import org.apache.commons.codec.digest.DigestUtils;

import bitTorrent.tracker.protocol.udp.messages.custom.SHA1;
import tracker.Const;

/**
 * Holds a triplet of (info_hash, host and port) used in HelloResponseM
 * messages.
 * @author Irene
 * @author Jesus 
 */
public class Contents {
	private SHA1 info_hash;
	private int host;
	private short port;
	
	public Contents(SHA1 info_hash, int host, short port) {
		this.info_hash = info_hash;
		this.host = host;
		this.port = port;
	}
	
	public SHA1 getInfo_hash() {
		return info_hash;
	}
	public int getHost() {
		return host;
	}

	public short getPort() {
		return port;
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
		if (Const.PRINTF_BYTES) {
			System.out.print("\n[Contents] : ");
			String print = "";
			for (byte i : ret)
				print += String.format("0x%02X ", i);
			System.out.println(print + "\n");
		}
		return ret;
	}
	
	public int getSize() {
		return 16 + 4 + 2;
	}
	
	public String toString() {
		return "{ info_hash: " + info_hash.toString() + ", host: "
				+ host + ", port: " + port + " }";
	}

	public String prepareForHash() {
		return new String(info_hash.getBytes()) + Integer.toString(host) 
			+ Short.toString(port);
	}
	
	public static void main(String [] args) throws Exception {
		Contents c = new Contents(new SHA1(DigestUtils.sha1("Hello world")),
				1, (short) 1);
		byte [] bytes = c.getBytes();
		System.out.print("\n[Contents] : ");
		String print = "";
		for (byte i : bytes)
			print += String.format("0x%02X ", i);
		System.out.println(print + "\n");
	}
}
