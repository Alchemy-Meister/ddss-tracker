package tracker.networking;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import bitTorrent.tracker.protocol.udp.messages.custom.CustomMessage;
import bitTorrent.tracker.protocol.udp.messages.custom.ka.KeepAliveM;
import tracker.Const;
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
			// read until 0x0A 0x0D
			boolean found = false;
			int pos = 4;
			while(!found && pos < bytes.length) {
				if (bytes[pos] == 0x0A) {
					if (pos + 1 <= bytes.length - 1) {
						if (bytes[pos + 1] == 0x0D)
							found = true;
						else
							pos++;

					} else
						pos++;
				} else
					pos++;
			}
			if (found) { // we are at 0x0A
				byte id[] = new byte[pos - 4];
				System.arraycopy(bytes, 4, id, 0, pos - 4);
				if (Const.PRINTF) {
					System.out.print("[PaPa] read id: ");
					for (byte i : id)
						System.out.printf("0x%02X ", i);
					System.out.println();
				}
				BigInteger bid = new BigInteger(id);
				return new KeepAliveM(bid);
			} else
				throw new PacketParserException("0x0A 0x0D not found on KA");
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
