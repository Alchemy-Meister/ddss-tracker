package bitTorrent.tracker.protocol.udp.messages;

/**
 *
 * Offset  Size            	Name            	Value
 * 0       64-bit integer  	connection_id   	0x41727101980 (default value)
 * 8       32-bit integer  	action          	0 // connect
 * 12      32-bit integer  	transaction_id
 * 16
 * 
 */

public class ConnectRequest extends BitTorrentUDPRequestMessage {
	
	public ConnectRequest() {
		super(Action.CONNECT);
		super.setConnectionId(Long.decode("0x41727101980"));
	}
	
	@Override
	public byte[] getBytes() {
		//TODO: Complete this method using ByteBuffer class
		
		return null;
	}
	
	public static ConnectRequest parse(byte[] byteArray) {
		//TODO: Complete this method using ByteBuffer class
		
		return null;
	}
}