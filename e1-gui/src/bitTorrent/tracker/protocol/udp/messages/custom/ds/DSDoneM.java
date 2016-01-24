package bitTorrent.tracker.protocol.udp.messages.custom.ds;

import java.nio.ByteBuffer;

import bitTorrent.tracker.protocol.udp.messages.custom.CustomMessage;
import bitTorrent.tracker.protocol.udp.messages.custom.LongLong;
import bitTorrent.tracker.protocol.udp.messages.custom.Type;
import tracker.Const;

/**
 * Offset  Size	 			Name   			Value
 * 0       32-bit integer	type   			5 // ds-done message
 * 4       64-bit integer   transaction_id
 * 12 	   128-bit integer  sender's ID
 * @author Irene
 * @author Jesus
 *
 */
public class DSDoneM extends DatabaseSyncM {

	private LongLong id;
	
	public DSDoneM(long connection_id, LongLong id) {
		// IMPORTANT NOTE!!!!
		// we use the connection_id to put the TRANSACTION_ID
		super(Type.DS_DONE, connection_id);
		this.id = id;
	}

	public LongLong getSenderId() {
		return id;
	}
	
	@Override
	public byte[] getBytes() {
		byte[] typeBytes = ByteBuffer.allocate(4).putInt(
				this.type.getValue()).array();
		byte[] connectionIdBytes = ByteBuffer.allocate(8).putLong(
				connection_id).array();
		byte[] idbytes = id.getBytes(); 
		byte[] ret = new byte[typeBytes.length + connectionIdBytes.length
		                      + idbytes.length + CustomMessage.CRLF.length];
		System.arraycopy(typeBytes, 0, ret, 0, typeBytes.length);
		System.arraycopy(connectionIdBytes, 0, ret, typeBytes.length,
				connectionIdBytes.length);
		System.arraycopy(idbytes, 0, ret, typeBytes.length +
				connectionIdBytes.length, idbytes.length);
		System.arraycopy(CustomMessage.CRLF, 0, ret, typeBytes.length
				 + connectionIdBytes.length + idbytes.length,
				 CustomMessage.CRLF.length);
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
		return "[type: " + type.getValue() + ", transaction_id:" +
				connection_id + ", id:" + id.toString() + "]";
	}

}
