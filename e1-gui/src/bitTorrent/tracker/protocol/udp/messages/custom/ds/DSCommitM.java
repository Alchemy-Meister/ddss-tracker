package bitTorrent.tracker.protocol.udp.messages.custom.ds;

import java.nio.ByteBuffer;

import bitTorrent.tracker.protocol.udp.messages.custom.CustomMessage;
import bitTorrent.tracker.protocol.udp.messages.custom.Type;
import tracker.Const;

/**
 * Offset  Size	 			Name   			Value
 * 0       32-bit integer	type   			4 // ds-commit message
 * 4       64-bit integer   connection_id
 * ---------------------------------------------------------------
 * 12	   32-bit integer   action
 * 16      32-bit integer   transaction_id
 * @author Irene
 * @author Jesus
 *
 */
public class DSCommitM extends DatabaseSyncM {
	private static final long serialVersionUID = -3196536417950109597L;
	private int action;
	private int transaction_id;

	public DSCommitM(long connection_id, int action, int transaction_id) {
		super(Type.DS_COMMIT, connection_id);
		this.action = action;
		this.transaction_id = transaction_id;
	}
	
	public int getTransactionId() {
		return this.transaction_id;
	}

	@Override
	public byte[] getBytes() {
		byte[] typeBytes = ByteBuffer.allocate(4).putInt(
				this.type.getValue()).array();
		byte[] connectionIdBytes = ByteBuffer.allocate(8).putLong(
				connection_id).array();
		byte[] actionBytes = ByteBuffer.allocate(4).putInt(this.action).array();
		byte[] transBytes = ByteBuffer.allocate(4).putInt(
				this.transaction_id).array();
		byte[] ret = new byte[typeBytes.length + connectionIdBytes.length
		                       + actionBytes.length + transBytes.length
		                       + CustomMessage.CRLF.length];
		System.arraycopy(typeBytes, 0, ret, 0, typeBytes.length);
		System.arraycopy(connectionIdBytes, 0, ret, typeBytes.length,
				connectionIdBytes.length);
		System.arraycopy(actionBytes, 0, ret,
				typeBytes.length + connectionIdBytes.length,
				actionBytes.length);
		System.arraycopy(transBytes, 0, ret,
				typeBytes.length + connectionIdBytes.length + actionBytes.length,
				transBytes.length);
		System.arraycopy(CustomMessage.CRLF, 0,	ret,
				typeBytes.length + connectionIdBytes.length
				+ actionBytes.length + transBytes.length,
				CustomMessage.CRLF.length);
		if (Const.PRINTF_BYTES) {
			System.out.print("[ DS-C ] HEX: ");
			for (byte i : ret)
				System.out.printf("0x%02X ", i);
			System.out.println();
		}
		return ret;
	}

	@Override
	public String toString() {
		return "[type: " + type.getValue() + ", connection_id:" +
				connection_id + ", action: " + action + ", transaction_id: " +
				transaction_id + "]";
	}

}
