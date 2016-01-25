package bitTorrent.tracker.protocol.udp.messages.custom.hi;

import java.nio.ByteBuffer;

import org.apache.commons.codec.digest.DigestUtils;

import bitTorrent.tracker.protocol.udp.messages.custom.CustomMessage;
import bitTorrent.tracker.protocol.udp.messages.custom.LongLong;
import bitTorrent.tracker.protocol.udp.messages.custom.SHA1;
import tracker.Const;

/**
 * Offset  Size	 			Name   			Value
 * 0       32-bit integer	type   		    2 // hi message
 * 4       64-bit integer   connection_id   
 * --
 * 12	   128-bit integer  assigned_id
 * 28      160-bit integer  contents_sha
 * @author Irene
 * @author Jesus
 *
 */
public class HelloCloseM extends HelloBaseM {

	private static final long serialVersionUID = -1051799110833112540L;
	private LongLong assigned_id;
	private SHA1 contents_sha;
	public static final String HI_CLOSE = "HI_CLOSE";
	
	public HelloCloseM(long connection_id, LongLong assigned_id,
			SHA1 contents_sha)
	{
		super(connection_id, HI_CLOSE);
		this.assigned_id = assigned_id;
		this.contents_sha = contents_sha;
	}

	public LongLong getAssigned_id() {
		return assigned_id;
	}

	public void setAssigned_id(LongLong assigned_id) {
		this.assigned_id = assigned_id;
	}

	public SHA1 getContents_sha() {
		return contents_sha;
	}

	public void setContents_sha(SHA1 contents_sha) {
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
		if (Const.PRINTF_BYTES) {
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
	
	public static void main(String [] args) throws Exception {
		HelloCloseM m = new HelloCloseM(2222, new LongLong("34"),
				new SHA1(DigestUtils.sha1("Hello")));
		System.out.println(m.getBytes().length); // 50
	}

}
