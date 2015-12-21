package tracker.networking;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import bitTorrent.tracker.protocol.udp.messages.custom.CustomMessage;
import bitTorrent.tracker.protocol.udp.messages.custom.LongLong;
import bitTorrent.tracker.protocol.udp.messages.custom.ds.DSCommitM;
import bitTorrent.tracker.protocol.udp.messages.custom.ds.DSDoneM;
import bitTorrent.tracker.protocol.udp.messages.custom.ds.DSReadyM;
import bitTorrent.tracker.protocol.udp.messages.custom.hi.Contents;
import bitTorrent.tracker.protocol.udp.messages.custom.hi.HelloCloseM;
import bitTorrent.tracker.protocol.udp.messages.custom.hi.HelloM;
import bitTorrent.tracker.protocol.udp.messages.custom.hi.HelloResponseM;
import bitTorrent.tracker.protocol.udp.messages.custom.ka.KeepAliveM;
import bitTorrent.tracker.protocol.udp.messages.custom.me.MasterElectionM;
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
		if (Const.PRINTF) {
			System.out.print("\n[PaPa] got: ");
			String print = "";
			for (byte i : bytes)
				print += String.format("0x%02X ", i);
			System.out.println(print + "\n");
		}
		ByteBuffer messageBytes = ByteBuffer.wrap(bytes);
		// Identify the type of the message - 32 bits
		int type = messageBytes.getInt(0); // 0 - 32 bit integer
		switch(type) {
		case 0: // KA
			// read until 0x0A 0x0D
			int tempPos = getCRLFpos(bytes, 4);
			if (tempPos != -1) { // we are at 0x0A
				byte id[] = new byte[tempPos - 4];
				System.arraycopy(bytes, 4, id, 0, tempPos - 4);
				if (Const.PRINTF) {
					System.out.print("\n[PaPa] read id: ");
					for (byte i : id)
						System.out.printf("0x%02X ", i);
					System.out.println();
				}
				return new KeepAliveM(new LongLong(id));
			} else
				throw new PacketParserException("0x0A 0x0D not found on KA");
		case 1: // ME
			// ME messages are similar to KA
			tempPos = getCRLFpos(bytes, 4);
			if (tempPos != -1) { // we are at 0x0A
				byte payload[] = new byte[tempPos - 4];
				System.arraycopy(bytes, 4, payload, 0, tempPos - 4);
				if (Const.PRINTF) {
					System.out.print("\n[PaPa] read payload: ");
					for (byte i : payload)
						System.out.printf("0x%02X ", i);
					System.out.println();
				}
				return new MasterElectionM(new LongLong(payload));
			} else
				throw new PacketParserException("0x0A 0x0D not found on ME");
		case 2: // HI
			// three types of Hi messages {HelloM, HelloRes, HelloClose}
			tempPos = getCRLFpos(bytes, 4);
			if (tempPos != -1) {
				// x x x 0x0A 0x0D trash trash trash trash
				//       || trash + 0x0A 0x0D size -> length - pos of 0x0A
				//       || actual size -> length - trash size
				int actualSize = bytes.length - (bytes.length - tempPos);
				// Connection id always
				long connection_id = messageBytes.getLong(4);
				if (12 == actualSize) {
					System.out.println("[HelloM message]");
					return new HelloM(connection_id);
				} else {
					// if size > 12 we may have a a hi response or a hi close
					// they both have assigned id
					byte assigned_id[] = new byte[16];
					System.arraycopy(bytes, 4 + 8, assigned_id, 0, 16);
					if (Const.PRINTF) {
						for (byte i : assigned_id)
							System.out.printf("0x%02X ", i);
						System.out.println();
					}
					// and contents sha
					byte contents_sha[] = new byte[16];
					System.arraycopy(bytes, 4 + 8 + 16, contents_sha, 0, 16);
					if (Const.PRINTF) {
						for (byte i : contents_sha)
							System.out.printf("0x%02X ", i);
						System.out.println();
					}
					// when the message does not have more data is a close
					if (44 == actualSize) {
						System.out.println("[HelloClose message]");
						return new HelloCloseM(connection_id,
								new LongLong(assigned_id),
								new LongLong(contents_sha));
					} else {
						List<Contents> contents = new ArrayList<Contents>();
						int offset = 4 + 8 + 16 + 16;
						while (offset < tempPos - 1) {
							byte info_hash[] = new byte[16];
							System.arraycopy(bytes, offset, info_hash,0, 16);
							offset += 16;
							int host = messageBytes.getInt(offset);
							offset += 4;
							short port = messageBytes.getShort(offset);
							offset += 2;
							contents.add(new Contents(new LongLong(info_hash),
									host, port));
						}
						System.out.println("[HelloResponse message]");
						return new HelloResponseM(connection_id,
									new LongLong(assigned_id),
									new LongLong(contents_sha), contents);
					}
				}
			} else
				throw new PacketParserException("0x0a 0x0D not found on HI");
		case 3: // DS_READY
			tempPos = getCRLFpos(bytes, 4);
			if (tempPos != -1) { // we are at 0x0A
				long connection_id = messageBytes.getLong(4);
				int action = messageBytes.getInt(12);
				int transaction_id = messageBytes.getInt(16);
				int current_post = 16 + 4;
				List<LongLong> infohashes = new ArrayList<LongLong>();
				while (current_post < tempPos) {
					byte[] longlong = new byte[16];
					System.arraycopy(bytes, current_post, longlong, 0, 16);
					infohashes.add(new LongLong(longlong));
					current_post += 16;
				}
				return new DSReadyM(connection_id, action, transaction_id,
						infohashes);
			} else
				throw new PacketParserException("0x0A 0x0D not found on DS-C");
		case 4: // DS_COMMIT
			tempPos = getCRLFpos(bytes, 4);
			if (tempPos != -1) { // we are at 0x0A
				long connection_id = messageBytes.getLong(4);
				int action = messageBytes.getInt(12);
				int transaction_id = messageBytes.getInt(16);
				return new DSCommitM(connection_id, action, transaction_id);
			} else
				throw new PacketParserException("0x0A 0x0D not found on DS-C");
		case 5: // DS_DONE
			tempPos = getCRLFpos(bytes, 4);
			if (tempPos != -1) { // we are at 0x0A
				long connection_id = messageBytes.getLong(4);
				return new DSDoneM(connection_id);
			} else
				throw new PacketParserException("0x0A 0x0D not found on DS-D");
		default:

		}
		return null;
	}
	
	/** Given an array of bytes returns the position where 0x0A 0x0D begins.
	 * @param bytes - byte array
	 * @param pos - position to start looking from
	 * @return position if found, else -1
	 */
	private static int getCRLFpos(byte[] bytes, int pos) {
		boolean found = false;
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
		return found ? pos : -1;
	}

}
