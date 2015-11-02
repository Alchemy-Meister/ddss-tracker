package bitTorrent.tracker.protocol.udp.messages.custom.ka;

import java.math.BigInteger;

import bitTorrent.tracker.protocol.udp.messages.custom.Type;

/**
 * Offset  Size	 			Name   Value
 * 0       32-bit integer	type   0 // ka message
 * 4       128-bit integer  ID
 * @author Irene
 * @author Jesus
 */
public class KeepAliveM {

	private final Type type = Type.KA;
	private BigInteger id;
	
	public KeepAliveM(BigInteger id) {
		this.id = id;
	}

	public BigInteger getId() {
		return id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public Type getType() {
		return type;
	}
	
}
