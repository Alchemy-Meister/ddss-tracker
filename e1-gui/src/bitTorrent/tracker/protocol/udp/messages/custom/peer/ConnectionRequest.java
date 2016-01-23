package bitTorrent.tracker.protocol.udp.messages.custom.peer;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;

import bitTorrent.tracker.protocol.udp.messages.BitTorrentUDPRequestMessage;

/**
*
* Offset  Size    			Name    			Value
* 0       64-bit integer  	connection_id
* 8       32-bit integer  	action          	1 // announce
* 12      32-bit integer  	transaction_id
* 
*/

public class ConnectionRequest extends BitTorrentUDPRequestMessage {
	
	private static String CONNECTION_ID_STRING = "FEE1DEADBAADBEEF";
	
	private Random random = new Random();
	
	public ConnectionRequest() {
		super(Action.CONNECT);
		
		this.setConnectionId(new BigInteger(
				CONNECTION_ID_STRING, 16).longValue());
		this.setTransactionId(random.nextInt());
	}
	
	public byte[] getBytes() {
		ByteBuffer byteBuffer = ByteBuffer.allocate(16);
		byteBuffer.order(ByteOrder.BIG_ENDIAN);
		
		byteBuffer.putLong(0, this.getConnectionId());
		byteBuffer.putInt(8, this.getAction().value());
		byteBuffer.putInt(12, this.getTransactionId());
		
		byteBuffer.flip();
		
		return byteBuffer.array();
	}
	
	public static ConnectionRequest parse(byte[] byteArray) {
		ByteBuffer bufferReceive = ByteBuffer.wrap(byteArray);
		ConnectionRequest request = new ConnectionRequest();
		
		request.setConnectionId(bufferReceive.getLong(0));
		request.setAction(Action.valueOf(bufferReceive.getInt(8)));
		request.setTransactionId(bufferReceive.getInt(12));
		
		return request;
	}

	@Override
	public String toString() {
		return "CR:\r\n\tConnection_id: " + this.getConnectionId() +
				"\r\n\tAction: " + this.getAction() +
				"\r\n\tTransaction_id: " + this.getTransactionId();
	}
}