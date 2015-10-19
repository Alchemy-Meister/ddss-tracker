package bitTorrent.tracker.protocol.udp.messages;

import java.util.ArrayList;
import java.util.List;

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
		//TODO: Complete this method using ByteBuffer class
		
		return null;
	}
	
	public static AnnounceResponse parse(byte[] byteArray) {
		//TODO: Complete this method using ByteBuffer class
		
		return null;
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