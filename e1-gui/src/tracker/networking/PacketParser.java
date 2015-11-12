package tracker.networking;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import bitTorrent.tracker.protocol.udp.messages.custom.CustomMessage;
import bitTorrent.tracker.protocol.udp.messages.custom.ka.KeepAliveM;
import tracker.exceptions.PacketParserException;

/**
 * Parsers bytes to domain objects.
 * @author Irene
 * @author Jesus
 */
public class PacketParser {

	
	/** Parses the bytes and returns a CustomMessage.
	 * @param bytes
	 * @return
	 * @throws PacketParserException when the message is corrupt
	 */
	// TODO may change when we are geting peer messages
	public static CustomMessage parse(byte [] bytes) 
			throws PacketParserException
	{
		ByteBuffer messageBytes = ByteBuffer.wrap(bytes);
		// Identify the type of the message - 32 bits
		int type = messageBytes.getInt(0); // 0 - 32 bit integer
		switch(type) {
		case 0: // KA
			byte[] temp = new byte[128];
			messageBytes.get(temp, 4, 16);
			BigInteger id = new BigInteger(temp);
			return new KeepAliveM(id);
		case 1: // ME
			break;
		case 2: // HI
			break;
		case 3: // DS_READY
			break;
		case 4: // DS_COMMIT
			break;
		case 5: // DS_DONE
			break;
		default:
			
		}
		return null;
	}

}
