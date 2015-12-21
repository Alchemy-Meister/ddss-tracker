package bitTorrent.tracker.protocol.udp.messages.custom.hi;

import java.nio.ByteBuffer;

import bitTorrent.tracker.protocol.udp.messages.custom.CustomMessage;
import bitTorrent.tracker.protocol.udp.messages.custom.LongLong;
import tracker.Const;

/**
 * Offset  Size	 			Name   			Value
 * 0       32-bit integer	type   		    2 // hi message
 * 4       64-bit integer   connection_id   
 * --
 * 12	   128-bit integer  assigned_id
 * 28      128-bit integer  contents_sha
 * @author Irene
 * @author Jesus
 *
 */
public class HelloCloseM extends HelloBaseM {

	private LongLong assigned_id;
	private LongLong contents_sha;
	
	public HelloCloseM(long connection_id, LongLong assigned_id,
			LongLong contents_sha)
	{
		super(connection_id);
		this.assigned_id = assigned_id;
		this.contents_sha = contents_sha;
	}

	public LongLong getAssigned_id() {
		return assigned_id;
	}

	public void setAssigned_id(LongLong assigned_id) {
		this.assigned_id = assigned_id;
	}

	public LongLong getContents_sha() {
		return contents_sha;
	}

	public void setContents_sha(LongLong contents_sha) {
		this.contents_sha = contents_sha;
	}

	@Override
	public byte[] getBytes() {
		byte[] typeBytes = ByteBuffer.allocate(4).putInt(
				this.type.getValue()).array();
		byte[] connIdBytes = ByteBuffer.allocate(8).putLong(
				this.connection_id).array();
		byte[] assigned_id = this.assigned_id.getBytes();
		byte[] contents = this.contents_sha.getBytes();
		byte[] ret = new byte[typeBytes.length + connIdBytes.length +
		                      assigned_id.length + contents.length
		                       + CustomMessage.CRLF.length];
		System.arraycopy(typeBytes, 0, ret, 0, typeBytes.length);
		System.arraycopy(connIdBytes, 0, ret, typeBytes.length,
				connIdBytes.length);
		System.arraycopy(assigned_id, 0, ret,
				typeBytes.length + connIdBytes.length,
				assigned_id.length);
		System.arraycopy(contents, 0, ret,
				typeBytes.length + connIdBytes.length + assigned_id.length,
				contents.length);
		System.arraycopy(CustomMessage.CRLF, 0,	ret,
				typeBytes.length + connIdBytes.length + assigned_id.length
				+ contents.length, CustomMessage.CRLF.length);
		if (Const.PRINTF) {
			System.out.print("[ HI C ] HEX: ");
			for (byte i : ret)
				System.out.printf("0x%02X ", i);
			System.out.println();
		}
		return ret;
	}

	@Override
	public String toString() {
		return "[type: " + this.type.getValue() + ", connection_id: "
				+ this.connection_id + ", assigned_id: "
				+ this.assigned_id.toString() + ", contents_sha: "
				+ this.contents_sha.toString() + "]";
	}

}
