package bitTorrent.tracker.protocol.udp.messages.custom.peer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import bitTorrent.tracker.protocol.udp.messages.BitTorrentUDPMessage;
import bitTorrent.tracker.protocol.udp.messages.PeerInfo;

/**
 * 
 * Offset      Size            	Name            Value
 * 0           32-bit integer  	action          1 // announce
 * 4           32-bit integer  	transaction_id
 * 8           32-bit integer  	interval
 * 12          32-bit integer  	leechers
 * 16          32-bit integer  	seeders
 * 20 + 6 * n  32-bit integer  	IP address
 * 24 + 6 * n  16-bit integer  	TCP port		(this is an unsigned int, so we use a Java char)
 * 20 + 6 * N
 * 
 */

public class AnnounceResponse extends BitTorrentUDPMessage {
	
	private int interval;
	private int leechers;
	private int seeders;
	
	private List<PeerInfo> peers;
	
	public AnnounceResponse() {
		super(Action.ANNOUNCE);
		
		this.peers = new ArrayList<>();
	}
	
	@Override
	public byte[] getBytes() {
		int size = 20 + 6 * peers.size();
		ByteBuffer byteBuffer = ByteBuffer.allocate(size);
		byteBuffer.order(ByteOrder.BIG_ENDIAN);

		byteBuffer.putInt(0, this.getAction().value());
		byteBuffer.putInt(4, this.getTransactionId());
		byteBuffer.putInt(8, this.getInterval());
		byteBuffer.putInt(12, this.getLeechers());
		byteBuffer.putInt(16, this.getSeeders());
		
		int offset = 20;
		for (PeerInfo peerInfo : peers) {
			byteBuffer.putInt(offset, peerInfo.getIpAddress());
			offset += 4;
			
			byteBuffer.putChar(offset, (char) peerInfo.getPort());
			offset += 2;
		}
		
		byteBuffer.flip();
		
		return byteBuffer.array();
	}
	
	public static AnnounceResponse parse(byte[] byteArray) {
		ByteBuffer bufferReceive = ByteBuffer.wrap(byteArray);
		AnnounceResponse response = new AnnounceResponse();
		
		response.setAction(Action.valueOf(bufferReceive.getInt(0) ));
		response.setTransactionId(bufferReceive.getInt(4));
		response.setInterval(bufferReceive.getInt(8));
		response.setLeechers(bufferReceive.getInt(12));
		response.setSeeders(bufferReceive.getInt(16));
		List<PeerInfo> peers = new ArrayList<PeerInfo>();
		
		int offset;
		
		for (offset = 20; offset < byteArray.length; offset += 6) {
			int ipAddress = bufferReceive.getInt(offset);
			char port = bufferReceive.getChar(offset+4);
			PeerInfo peerInfo = new PeerInfo();
			peerInfo.setIpAddress(ipAddress);
			peerInfo.setPort(port);
			peers.add(peerInfo);
		}
		response.setPeers(peers);
		
		return response;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public int getLeechers() {
		return leechers;
	}

	public void setLeechers(int leechers) {
		this.leechers = leechers;
	}

	public int getSeeders() {
		return seeders;
	}

	public void setSeeders(int seeders) {
		this.seeders = seeders;
	}

	public List<PeerInfo> getPeers() {
		return peers;
	}

	public void setPeers(List<PeerInfo> peers) {
		this.peers = peers;
	}
}