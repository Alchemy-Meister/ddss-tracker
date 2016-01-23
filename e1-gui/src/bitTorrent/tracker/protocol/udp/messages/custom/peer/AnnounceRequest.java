package bitTorrent.tracker.protocol.udp.messages.custom.peer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;

import bitTorrent.tracker.protocol.udp.messages.BitTorrentUDPRequestMessage;
import bitTorrent.tracker.protocol.udp.messages.PeerInfo;
import common.utils.Utilities;

/**
 *
 * Offset  Size    			Name    			Value
 * 0       64-bit integer  	connection_id
 * 8       32-bit integer  	action          	1 // announce
 * 12      32-bit integer  	transaction_id
 * 16      20-byte string  	info_hash
 * 36      20-byte string  	peer_id
 * 56      64-bit integer  	downloaded
 * 64      64-bit integer  	left
 * 72      64-bit integer  	uploaded
 * 80      32-bit integer  	event           	0 // 0: none; 1: completed; 2: started; 3: stopped
 * 84      32-bit integer  	IP address      	-1 // default
 * 88      32-bit integer  	key					Random value
 * 92      32-bit integer  	num_want        	-1 // default
 * 96      16-bit integer  	port				(this is an unsigned int)
 * 
 * 
 */

public class AnnounceRequest extends BitTorrentUDPRequestMessage {

	public enum Event {		
		NONE(0),
		COMPLETED(1),
		STARTED(2),
		STOPPED(3);
		
		private int value;
		
		private Event(int value) {
			this.value = value;
		}
		
		public int value() {
			return this.value;
		}
	}
	
	private static Random random = new Random();
	
	private String infoHash;
	private String peerId;
	private long downloaded;
	private long left;
	private long uploaded;
	private Event event;
	private int key;
	private int numWant = -1;
	
	private PeerInfo peerInfo;
	
	public AnnounceRequest() {
		super(Action.ANNOUNCE);
		
		//Generate a random Peer ID
		this.peerId = AnnounceRequest.createPeerId();
		//Key is a random value
		this.key = AnnounceRequest.random.nextInt(Integer.MAX_VALUE);
		this.peerInfo = new PeerInfo();
		this.peerInfo.setIpAddress(-1);
	}
	
	@Override
	public byte[] getBytes() {
		ByteBuffer byteBuffer = ByteBuffer.allocate(98);
		byteBuffer.order(ByteOrder.BIG_ENDIAN);
		
		byteBuffer.putLong(0, this.getConnectionId());
		byteBuffer.putInt(8, this.getAction().value());
		byteBuffer.putInt(12, this.getTransactionId());
		byteBuffer.position(16);
		byteBuffer.put(infoHash.getBytes());
		byteBuffer.position(36);
		byteBuffer.put(peerId.getBytes());
		byteBuffer.putLong(56, downloaded);
		byteBuffer.putLong(64, left );
		byteBuffer.putLong(72, uploaded);
		byteBuffer.putInt(80, this.getEvent().value());
		byteBuffer.putInt(84,peerInfo.getIpAddress());
		byteBuffer.putInt(88,key);
		byteBuffer.putInt(92, numWant);
		byteBuffer.putChar(96, (char) peerInfo.getPort());
		
		byteBuffer.flip();
		
		return byteBuffer.array();
	}
	
	public static AnnounceRequest parse(byte[] byteArray) {
		ByteBuffer bufferReceive = ByteBuffer.wrap(byteArray);
		AnnounceRequest request = new AnnounceRequest();
		
		request.setConnectionId(bufferReceive.getLong(0));
		request.setAction(Action.valueOf(bufferReceive.getInt(8)));
		request.setTransactionId(bufferReceive.getInt(12));
		
		byte [] infoHash = new byte[20];
		bufferReceive.position(16);
		bufferReceive.get(infoHash);
		request.setInfoHash(Utilities.toHexString(infoHash)); 
		
		byte [] peerId = new byte[20];
		bufferReceive.position(36);
		bufferReceive.get(peerId);
		request.setPeerId(new String(peerId));
		request.setDownloaded(bufferReceive.getLong(56));
		request.setLeft(bufferReceive.getLong(64));
		request.setUploaded(bufferReceive.getLong(72));
		request.setEvent(
				Event.valueOf(String.valueOf(bufferReceive.getInt(80))));
		PeerInfo peerInfo = new PeerInfo();
		peerInfo.setIpAddress(bufferReceive.getInt(84));
		peerInfo.setPort(bufferReceive.getChar(96));
		request.setPeerInfo(peerInfo);
		
		request.setKey(bufferReceive.getInt(88));
		request.setNumWant(bufferReceive.getInt(92));
		
		return request;
	}

	public String getInfoHash() {
		return infoHash;
	}

	public void setInfoHash(String infoHash) {
		this.infoHash = infoHash;
	}

	public String getPeerId() {
		return peerId;
	}

	public void setPeerId(String peerId) {
		this.peerId = peerId;
	}

	public long getDownloaded() {
		return downloaded;
	}

	public void setDownloaded(long downloaded) {
		this.downloaded = downloaded;
	}

	public long getLeft() {
		return left;
	}

	public void setLeft(long left) {
		this.left = left;
	}

	public long getUploaded() {
		return uploaded;
	}

	public void setUploaded(long uploaded) {
		this.uploaded = uploaded;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public int getNumWant() {
		return numWant;
	}

	public void setNumWant(int numWant) {
		this.numWant = numWant;
	}

	public PeerInfo getPeerInfo() {
		return peerInfo;
	}

	public void setPeerInfo(PeerInfo peerInfo) {
		this.peerInfo = peerInfo;
	}
	
	private static String createPeerId() {
		StringBuffer bufferID = new StringBuffer();
		
		bufferID.append("-");
		bufferID.append("SSDD01");
		bufferID.append("-");
		
		for(int i=0; i<12; i++) {
			bufferID.append(AnnounceRequest.random.nextInt(9));
		}
				
		return bufferID.toString();
	}
}