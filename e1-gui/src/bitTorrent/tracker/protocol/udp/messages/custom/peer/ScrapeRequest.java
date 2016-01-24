package bitTorrent.tracker.protocol.udp.messages.custom.peer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.xml.bind.DatatypeConverter;

import bitTorrent.tracker.protocol.udp.messages.BitTorrentUDPRequestMessage;

/**
 * 
 * Offset          Size            	Name            	Value
 * 0               64-bit integer  	connection_id
 * 8               32-bit integer  	action          	2 // scrape
 * 12              32-bit integer  	transaction_id
 * 16 + 20 * n     20-byte string  	info_hash
 * 16 + 20 * N
 *
 */

public class ScrapeRequest extends BitTorrentUDPRequestMessage {

	private List<String> infoHashes;
	
	public ScrapeRequest() {
		super(Action.SCRAPE);
		this.infoHashes = new ArrayList<>();
		this.setTransactionId(new Random().nextInt());
	}
	
	@Override
	public byte[] getBytes() {
		int size = 16 + 20 * infoHashes.size();
		
		ByteBuffer byteBuffer = ByteBuffer.allocate(size);
		byteBuffer.order(ByteOrder.BIG_ENDIAN);

		byteBuffer.putLong(0, this.getConnectionId());
		byteBuffer.putInt(8, this.getAction().value());
		byteBuffer.putInt(12, this.getTransactionId());
		
		int offset = 20;
		for (String infoHash : infoHashes) {
			byteBuffer.position(offset);
			byteBuffer.put(infoHash.getBytes());
			offset += 20;
		}
		
		return byteBuffer.array();
	}
	
	public static ScrapeRequest parse(byte[] byteArray) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
		ScrapeRequest scrapeRequest = new ScrapeRequest();
		
		scrapeRequest.setConnectionId(byteBuffer.getLong(0));
		scrapeRequest.setAction(Action.valueOf(byteBuffer.getInt(8)));
		scrapeRequest.setTransactionId(byteBuffer.getInt(12));
		
		int offset = 16;
		for (offset = 16; offset < byteArray.length; offset += 20) {
			byte[] infoHashBytes = new byte [20];
			byteBuffer.position(offset);
			byteBuffer.get(infoHashBytes);
			scrapeRequest.addInfoHash(
					DatatypeConverter.printHexBinary(infoHashBytes));
		}
		
		return scrapeRequest;
	}
	
	public List<String> getInfoHashes() {
		return infoHashes;
	}

	public void addInfoHash(String infoHash) {
		if (infoHash != null && !infoHash.trim().isEmpty() && !this.infoHashes.contains(infoHash)) {
			this.infoHashes.add(infoHash);
		}
	}
}