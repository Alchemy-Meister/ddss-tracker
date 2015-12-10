package bitTorrent.tracker.protocol.udp.messages.custom.ka;

import java.nio.ByteBuffer;

import bitTorrent.tracker.protocol.udp.messages.custom.CustomMessage;
import bitTorrent.tracker.protocol.udp.messages.custom.LongLong;
import bitTorrent.tracker.protocol.udp.messages.custom.Type;
import tracker.Const;

/**
 * Offset  Size	 			Name   Value
 * 0       32-bit integer	type   0 // ka message
 * 4       128-bit integer  ID
 * @author Irene
 * @author Jesus
 */
public class KeepAliveM extends CustomMessage {

	private final Type type = Type.KA;
	private LongLong id;

	public KeepAliveM() {
	}

	public KeepAliveM(LongLong id) {
		super();
		this.id = id;
	}

	public LongLong getId() {
		return id;
	}

	public void setId(LongLong id) {
		this.id = id;
	}

	public Type getType() {
		return type;
	}

	@Override
	public byte[] getBytes() {
		byte[] typeBytes = ByteBuffer.allocate(4).putInt(
				this.type.getValue()).array();
		byte[] idBytes = id.getBytes();
		byte[] ret = new byte[typeBytes.length + idBytes.length
		                       + CustomMessage.CRLF.length];
		System.arraycopy(typeBytes, 0, ret, 0, typeBytes.length);
		System.arraycopy(idBytes, 0, ret, typeBytes.length, idBytes.length);
		System.arraycopy(CustomMessage.CRLF, 0,	ret,
				typeBytes.length + idBytes.length, CustomMessage.CRLF.length);
		if (Const.PRINTF) {
			System.out.print("[ KA ] HEX: ");
			for (byte i : ret)
				System.out.printf("0x%02X ", i);
			System.out.println();
		}
		return ret;
	}

	@Override
	public String toString() {
		return "[type: " + this.type.getValue() + ", id: "
				+ this.id.toString() + "]";
	}


}
