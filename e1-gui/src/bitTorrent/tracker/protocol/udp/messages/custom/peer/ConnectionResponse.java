package bitTorrent.tracker.protocol.udp.messages.custom.peer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import bitTorrent.tracker.protocol.udp.messages.BitTorrentUDPRequestMessage;

/**
*
* Offset  Size    			Name    			Value
* 0       32-bit integer  	action  
* 4	      32-bit integer  	transaction_id
* 12      64-bit integer  	connection_id        	
* 
*/

public class ConnectionResponse extends BitTorrentUDPRequestMessage {
	
	public ConnectionResponse() {
		super(Action.CONNECT);
	}
	
	public ConnectionResponse(long connectionId, int transactionId) 
	{
		super(Action.CONNECT);
		this.setConnectionId(connectionId);
		this.setTransactionId(transactionId);
	}
	
	public byte[] getBytes() {
		ByteBuffer byteBuffer = ByteBuffer.allocate(16);
		byteBuffer.order(ByteOrder.BIG_ENDIAN);
		
		byteBuffer.putInt(0, this.getAction().value());
		byteBuffer.putInt(4, this.getTransactionId());
		byteBuffer.putLong(8, this.getConnectionId());
		
		byteBuffer.flip();
		
		return byteBuffer.array();
	}
	
	public static ConnectionResponse parse(byte[] byteArray) {
		ByteBuffer bufferReceive = ByteBuffer.wrap(byteArray);
		ConnectionResponse request = new ConnectionResponse();
		
		request.setAction(Action.valueOf(bufferReceive.getInt(0)));
		request.setTransactionId(bufferReceive.getInt(8));
		request.setConnectionId(bufferReceive.getLong(12));
		
		return request;
	}
}
