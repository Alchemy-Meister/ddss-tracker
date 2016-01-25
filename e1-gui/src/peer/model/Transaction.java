package peer.model;

import java.util.concurrent.ConcurrentHashMap;
import bitTorrent.tracker.protocol.udp.messages.BitTorrentUDPRequestMessage;

public class Transaction {
	
	public static ConcurrentHashMap<Integer,
		BitTorrentUDPRequestMessage> transactions = new ConcurrentHashMap<>();
}
