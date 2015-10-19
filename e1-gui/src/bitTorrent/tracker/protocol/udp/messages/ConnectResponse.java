package bitTorrent.tracker.protocol.udp.messages;

/**
 * 
 * Offset  Size            	Name            	Value
 * 0       32-bit integer  	action          	0 // connect
 * 4       32-bit integer  	transaction_id
 * 8       64-bit integer  	connection_id
 * 16
 * 
 */

public class ConnectResponse extends BitTorrentUDPRequestMessage {
	
	public ConnectResponse() {
		super(Action.CONNECT);
	}
	
	@Override
	public byte[] getBytes() {
		//TODO: Complete this method using ByteBuffer class
		
		return null;
	}
	
	public static ConnectResponse parse(byte[] byteArray) {
		//TODO: Complete this method using ByteBuffer class
		
		return null;
	}
}