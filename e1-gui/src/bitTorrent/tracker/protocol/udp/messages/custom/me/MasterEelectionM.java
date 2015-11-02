package bitTorrent.tracker.protocol.udp.messages.custom.me;

import java.math.BigInteger;

import bitTorrent.tracker.protocol.udp.messages.custom.Type;

/**
 * Offset  Size	 			Name     Value
 * 0       32-bit integer	type     1 // me message
 * 4       128-bit integer  payload
 * @author Irene
 * @author Jesus
 */
public class MasterEelectionM {

	private final Type type = Type.ME;
	private BigInteger payload;
	
	public MasterEelectionM(BigInteger payload) {
		this.payload = payload;
	}

	public BigInteger getPayload() {
		return payload;
	}

	public void setPayload(BigInteger payload) {
		this.payload = payload;
	}

	public Type getType() {
		return type;
	}
	
}
