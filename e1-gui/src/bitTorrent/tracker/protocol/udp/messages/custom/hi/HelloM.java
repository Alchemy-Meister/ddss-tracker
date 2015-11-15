package bitTorrent.tracker.protocol.udp.messages.custom.hi;

import java.nio.ByteBuffer;

import bitTorrent.tracker.protocol.udp.messages.custom.CustomMessage;
import tracker.Const;

/**
* Offset  Size	 			Name   			Value
* 0       32-bit integer	type   		    2 // hi message
* 4       64-bit integer   connection_id   
* @author Irene
* @author Jesus
*/
public class HelloM extends HelloBaseM {

	public HelloM(long connection_id) {
		super(connection_id);
	}

	@Override
	public byte[] getBytes() {
		byte[] typeBytes = ByteBuffer.allocate(4).putInt(
				this.type.getValue()).array();
		byte[] connectionIdBytes = ByteBuffer.allocate(8).putLong(
				this.connection_id).array();
		byte[] ret = new byte[typeBytes.length + connectionIdBytes.length
		                       + CustomMessage.CRLF.length];
		System.arraycopy(typeBytes, 0, ret, 0, typeBytes.length);
		System.arraycopy(connectionIdBytes, 0, ret, typeBytes.length,
				connectionIdBytes.length);
		System.arraycopy(CustomMessage.CRLF, 0,	ret,
				typeBytes.length + connectionIdBytes.length,
				CustomMessage.CRLF.length);
		if (Const.PRINTF) {
			System.out.print("[ HI B ] HEX: ");
			for (byte i : ret)
				System.out.printf("0x%02X ", i);
			System.out.println();
		}
		return ret;
	}

	@Override
	public String toString() {
		return "[type: " + this.type.getValue() + ", connection_id: "
				+ new Long(this.connection_id).toString() + "]";
	}

}
