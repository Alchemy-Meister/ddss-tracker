package bitTorrent.tracker.protocol.udp.messages.custom.me;

import java.nio.ByteBuffer;

import bitTorrent.tracker.protocol.udp.messages.custom.CustomMessage;
import bitTorrent.tracker.protocol.udp.messages.custom.LongLong;
import bitTorrent.tracker.protocol.udp.messages.custom.Type;
import tracker.Const;

/**
 * Offset  Size	 			Name     Value
 * 0       32-bit integer	type     1 // me message
 * 4       128-bit integer  payload
 * @author Irene
 * @author Jesus
 */
public class MasterElectionM extends CustomMessage {

	private final Type type = Type.ME;
	private LongLong payload;
	
	public MasterElectionM(LongLong payload) {
		this.payload = payload;
	}

	public LongLong getPayload() {
		return payload;
	}

	public void setPayload(LongLong payload) {
		this.payload = payload;
	}

	public Type getType() {
		return type;
	}

	@Override
	public byte[] getBytes() {
		byte[] typeBytes = ByteBuffer.allocate(4).putInt(
				this.type.getValue()).array();
		byte[] payloadBy = payload.getBytes();
		byte[] ret = new byte[typeBytes.length + payloadBy.length
		                      + CustomMessage.CRLF.length];
		System.arraycopy(typeBytes, 0, ret, 0, typeBytes.length);
		System.arraycopy(payloadBy, 0, ret, typeBytes.length, payloadBy.length);
		System.arraycopy(CustomMessage.CRLF, 0,	ret,
				typeBytes.length + payloadBy.length, CustomMessage.CRLF.length);
		if (Const.PRINTF_BYTES) {
			System.out.print("[ ME ] HEX: ");
			for (byte i : ret)
				System.out.printf("0x%02X ", i);
			System.out.println();
		}
		return ret;
	}

	@Override
	public String toString() {
		return "[type: " + this.type.getValue() + ", payload: "
				+ this.payload.toString() + "]";
	}
	
}
