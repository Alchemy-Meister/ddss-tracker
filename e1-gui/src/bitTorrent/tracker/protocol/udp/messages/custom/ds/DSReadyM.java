package bitTorrent.tracker.protocol.udp.messages.custom.ds;

import java.nio.ByteBuffer;
import java.util.List;

import bitTorrent.tracker.protocol.udp.messages.custom.CustomMessage;
import bitTorrent.tracker.protocol.udp.messages.custom.LongLong;
import bitTorrent.tracker.protocol.udp.messages.custom.SHA1;
import bitTorrent.tracker.protocol.udp.messages.custom.Type;
import tracker.Const;

/**
 * Offset  Size	 			Name   			Value
 * 0       32-bit integer	type   			3 // ds-ready message
 * 4       64-bit integer   connection_id
 * 12	   32-bit integer   action
 * 16      32-bit integer   transaction_id
 * 20 	   128-bit integer  sender's ID
 * 36      160-bit integer  info_hash       | Repeat, at least one
 * @author Irene
 * @author Jesus
 *
 */
public class DSReadyM extends DatabaseSyncM {
	
	private static final long serialVersionUID = -1438665336911280520L;
	private int action, transaction_id;
    private LongLong id;
    private List<SHA1> info_hashes;

	public DSReadyM(long connection_id, int action, int transaction_id,
			LongLong id, List<SHA1> info_hashes)
	{
		super(Type.DS_READY, connection_id);
		this.action = action;
		this.transaction_id = transaction_id;
		this.id = id;
		this.info_hashes = info_hashes;
	}
	
	public int getTransactionId() {
		return this.transaction_id;
	}
	
	public LongLong getId() {
		return this.id;
	}

	@Override
	public byte[] getBytes() {
		byte[] typeBytes = ByteBuffer.allocate(4).putInt(
				this.type.getValue()).array();
		byte[] connIdBytes = ByteBuffer.allocate(8).putLong(
				this.connection_id).array();
		byte[] actionbytes = ByteBuffer.allocate(4).putInt(this.action).array();
		byte[] transbytes = ByteBuffer.allocate(4).putInt(
				this.transaction_id).array();
		byte[] idbytes = id.getBytes();
		byte[] infohashesbytes = new byte[20 * info_hashes.size()];
		int offset = 0;
		for (SHA1 l : info_hashes) {
			System.arraycopy(l.getBytes(), 0, infohashesbytes, (offset * 20),
					20);
			offset++;
		}
		byte[] ret = new byte[typeBytes.length + connIdBytes.length
		                      + actionbytes.length + transbytes.length
		                      + idbytes.length + infohashesbytes.length
		                      + CustomMessage.CRLF.length];
		System.arraycopy(typeBytes, 0, ret, 0, typeBytes.length);
		System.arraycopy(connIdBytes, 0, ret, typeBytes.length,
				connIdBytes.length);
		System.arraycopy(actionbytes, 0, ret, typeBytes.length
				+ connIdBytes.length, actionbytes.length);
		System.arraycopy(transbytes, 0, ret, typeBytes.length
				 + connIdBytes.length + actionbytes.length, transbytes.length);
		System.arraycopy(idbytes, 0, ret, typeBytes.length
				 + connIdBytes.length + actionbytes.length + transbytes.length,
				 idbytes.length);
		System.arraycopy(infohashesbytes, 0, ret, typeBytes.length
				 + connIdBytes.length + actionbytes.length + transbytes.length
				 + idbytes.length,
				 infohashesbytes.length);
		System.arraycopy(CustomMessage.CRLF, 0, ret, typeBytes.length
				 + connIdBytes.length + actionbytes.length + transbytes.length
				 + idbytes.length + infohashesbytes.length,
				 CustomMessage.CRLF.length);
		if (Const.PRINTF_BYTES) {
			System.out.print("[ DS-R] HEX: ");
			for (byte i : ret)
				System.out.printf("0x%02X ", i);
			System.out.println();
		}
		return ret;
	}

	@Override
	public String toString() {
		String infos = "";
		boolean first = true;
		for (SHA1 l : info_hashes) {
			if (first){
				first = false;
				infos += l.toString(); 
			} else
				infos += ", " + l.toString();
		}
		
		return "[type: " + type.getValue() + ", connection_id:" +
		connection_id + ", action: " + action + ", transaction_id: " +
		transaction_id + ", info_hashes: { " + infos + " }]";
	}

}
