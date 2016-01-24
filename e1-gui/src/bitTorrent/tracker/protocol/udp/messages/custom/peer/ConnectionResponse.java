package bitTorrent.tracker.protocol.udp.messages.custom.peer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import bitTorrent.tracker.protocol.udp.messages.BitTorrentUDPRequestMessage;

/**
*
* Offset  Size    			Name    			Value
* 0       64-bit integer  	connection_id
* 8       32-bit integer  	action          	
* 12      32-bit integer  	transaction_id
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
		
		byteBuffer.putLong(0, this.getConnectionId());
		byteBuffer.putInt(8, this.getAction().value());
		byteBuffer.putInt(12, this.getTransactionId());
		
		byteBuffer.flip();
		
		return byteBuffer.array();
	}
	
	public static ConnectionResponse parse(byte[] byteArray) {
		ByteBuffer bufferReceive = ByteBuffer.wrap(byteArray);
		ConnectionResponse request = new ConnectionResponse();
		
		request.setConnectionId(bufferReceive.getLong(0));
		request.setAction(Action.valueOf(bufferReceive.getInt(8)));
		request.setTransactionId(bufferReceive.getInt(12));
		
		return request;
	}
}
