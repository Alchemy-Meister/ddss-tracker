package bitTorrent.tracker.protocol.udp.messages.custom.ds;

import java.nio.ByteBuffer;

import bitTorrent.tracker.protocol.udp.messages.custom.CustomMessage;
import bitTorrent.tracker.protocol.udp.messages.custom.Type;
import tracker.Const;

/**
 * Offset  Size	 			Name   			Value
 * 0       32-bit integer	type   			5 // ds-done message
 * 4       64-bit integer   connection_id
 * @author Irene
 * @author Jesus
 *
 */
public class DSDoneM extends DatabaseSyncM {

	public DSDoneM(long connection_id) {
		super(Type.DS_DONE, connection_id);
	}

	@Override
	public byte[] getBytes() {
		byte[] typeBytes = ByteBuffer.allocate(4).putInt(
				this.type.getValue()).array();
		byte[] connectionIdBytes = ByteBuffer.allocate(8).putLong(
				connection_id).array();
		byte[] ret = new byte[typeBytes.length + connectionIdBytes.length
		                      + CustomMessage.CRLF.length];
		System.arraycopy(typeBytes, 0, ret, 0, typeBytes.length);
		System.arraycopy(connectionIdBytes, 0, ret, typeBytes.length,
				connectionIdBytes.length);
		System.arraycopy(CustomMessage.CRLF, 0, ret, typeBytes.length
				 + connectionIdBytes.length, CustomMessage.CRLF.length);
		if (Const.PRINTF_BYTES) {
			System.out.print("[ DS-D] HEX: ");
			for (byte i : ret)
				System.out.printf("0x%02X ", i);
			System.out.println();
		}
		return ret;
	}

	@Override
	public String toString() {
		return "[type: " + type.getValue() + ", connection_id:" +
				connection_id + "]";
	}

}
