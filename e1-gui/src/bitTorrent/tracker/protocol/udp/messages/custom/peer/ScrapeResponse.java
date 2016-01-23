package bitTorrent.tracker.protocol.udp.messages.custom.peer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import bitTorrent.tracker.protocol.udp.messages.BitTorrentUDPMessage;
import bitTorrent.tracker.protocol.udp.messages.ScrapeInfo;

/**
 *
 * Offset      	Size            	Name            Value
 * 0           	32-bit integer  	action          2 // scrape
 * 4           	32-bit integer  	transaction_id
 * 8 + 12 * n  	32-bit integer  	seeders
 * 12 + 12 * n 	32-bit integer  	completed
 * 16 + 12 * n 	32-bit integer  	leechers
 * 8 + 12 * N
 * 
 */

public class ScrapeResponse extends BitTorrentUDPMessage {
	
	private List<ScrapeInfo> scrapeInfos;

	public ScrapeResponse() {
		super(Action.SCRAPE);		
		this.scrapeInfos = new ArrayList<>();
	}
	
	@Override
	public byte[] getBytes() {
		int size = 8 + 12 * scrapeInfos.size();
		
		ByteBuffer byteBuffer = ByteBuffer.allocate(size);
		byteBuffer.order(ByteOrder.BIG_ENDIAN);

		byteBuffer.putInt(0, this.getAction().value());
		byteBuffer.putInt(4, this.getTransactionId());
		
		int offset = 8;
		for (ScrapeInfo scrapeInfo : scrapeInfos) {
			byteBuffer.putInt(offset, scrapeInfo.getSeeders());
			offset += 4;
			byteBuffer.putInt (offset, scrapeInfo.getCompleted());
			offset += 4;
			byteBuffer.putInt(offset,scrapeInfo.getLeechers());
			offset += 4;
		}
		
		byteBuffer.flip();
		
		return byteBuffer.array();
	}
	
	public static ScrapeResponse parse(byte[] byteArray) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
		ScrapeResponse scrapeResponse = new ScrapeResponse();
		
		scrapeResponse.setAction(Action.valueOf(byteBuffer.getInt(0)));
		scrapeResponse.setTransactionId( byteBuffer.getInt(4));
		
		int offset;
		for (offset = 8; offset < byteArray.length; offset += 12) {
			ScrapeInfo scrapeInfo = new ScrapeInfo();
			scrapeInfo.setSeeders(byteBuffer.getInt(offset));
			scrapeInfo.setCompleted(byteBuffer.getInt(offset+4));
			scrapeInfo.setLeechers(byteBuffer.getInt(offset+8));
			
			scrapeResponse.addScrapeInfo(scrapeInfo);
		}
		
		return scrapeResponse;
	}
	
	public List<ScrapeInfo> getScrapeInfos() {
		return scrapeInfos;
	}

	public void addScrapeInfo(ScrapeInfo scrapeInfo) {
		if (scrapeInfo != null && !this.scrapeInfos.contains(scrapeInfo)) {
			this.scrapeInfos.add(scrapeInfo);
		}
	}
}