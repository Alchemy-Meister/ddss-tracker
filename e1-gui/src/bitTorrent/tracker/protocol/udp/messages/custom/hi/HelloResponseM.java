package bitTorrent.tracker.protocol.udp.messages.custom.hi;

import java.nio.ByteBuffer;
import java.util.List;

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
 * 28      128-bit integer  contents_sha
 * 44      160-bit integer  info_hash                         |
 * 64      32-bit integer   host							  | Repeat
 * 68      16-bit integer   port							  |
 * @author Irene
 * @author Jesus
 *
 */
public class HelloResponseM extends HelloBaseM {

	private LongLong assigned_id;
	private SHA1 contents_sha;
	private List<Contents> triplets;
	public static final String HI_RES = "HI_RES";


	public HelloResponseM(long connection_id, LongLong assigned_id,
			SHA1 contents_sha, List<Contents> triplets)
	{
		super(connection_id, HI_RES);
		this.assigned_id = assigned_id;
		this.contents_sha = contents_sha;
		this.triplets = triplets;
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
	
	public List<Contents> getTriplets() {
		return this.triplets;
	}

	@Override
	public byte[] getBytes() {
		byte[] typeBytes = ByteBuffer.allocate(4).putInt(
				this.type.getValue()).array();
		byte[] connectionIdBytes = ByteBuffer.allocate(8).putLong(
				this.connection_id).array();
		byte[] assignedIdBytes = this.assigned_id.getBytes();
		byte[] contentsBytes = this.contents_sha.getBytes();
		int temsi = -1;
		temsi = typeBytes.length + connectionIdBytes.length
				+ assignedIdBytes.length + contentsBytes.length 
				+ CustomMessage.CRLF.length;
		if (!this.triplets.isEmpty())
			temsi += (this.triplets.size() * this.triplets.get(0).getSize());
		byte[] ret = new byte[temsi];
		System.arraycopy(typeBytes, 0, ret, 0, typeBytes.length);
		System.arraycopy(connectionIdBytes, 0, ret, typeBytes.length,
				connectionIdBytes.length);
		System.arraycopy(assignedIdBytes, 0, ret,
				typeBytes.length + connectionIdBytes.length,
				assignedIdBytes.length);
		System.arraycopy(contentsBytes, 0, ret,
				typeBytes.length + connectionIdBytes.length
				+ assignedIdBytes.length,
				contentsBytes.length);
		int multiplier = 0;
		for (Contents triplet : this.triplets) {
			System.arraycopy(triplet.getBytes(), 0, ret,
					typeBytes.length + connectionIdBytes.length
					+ assignedIdBytes.length + contentsBytes.length
					+ (triplet.getSize() * multiplier), triplet.getSize());
			multiplier++;
		}
		if (this.triplets.isEmpty()) {
			System.arraycopy(CustomMessage.CRLF, 0, ret,
					typeBytes.length + connectionIdBytes.length
					+ assignedIdBytes.length + contentsBytes.length,
					CustomMessage.CRLF.length);

		} else {
			System.arraycopy(CustomMessage.CRLF, 0, ret,
					typeBytes.length + connectionIdBytes.length
					+ assignedIdBytes.length + contentsBytes.length
					+ (multiplier * this.triplets.get(0).getSize()),
					CustomMessage.CRLF.length);
		}

		if (Const.PRINTF_BYTES) {
			System.out.print("[ HI R ] HEX: ");
			for (byte i : ret)
				System.out.printf("0x%02X ", i);
			System.out.println();
		}
		return ret;
	}

	@Override
	public String toString() {
		String trip = "";
		boolean first = true;
		for (Contents s : this.triplets) {
			if (first) {
				trip += s.toString();
				first = false;
			} else
				trip += ", " + s.toString();
		}
		return "[type: " + this.type.getValue() + ", connection_id: "
		+ this.connection_id + ", assigned_id: "
		+ this.assigned_id.toString() + ", contents_sha: "
		+ this.contents_sha.toString() + ", contents: " + trip + "]";
	}

}
