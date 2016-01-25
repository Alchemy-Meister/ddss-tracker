package bitTorrent.tracker.protocol.udp.messages.custom.peer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import bitTorrent.tracker.protocol.udp.messages.BitTorrentUDPMessage;

/**
*
* Offset  Size    			Name    			Value
* 0   	  32-bit integer  	action          	
* 8       32-bit integer  	transaction_id
* 12	  32-bit integer	stringLength
* 16	  stringLength 		errorString
* 
*/

public class ResponseError extends BitTorrentUDPMessage {
	
	private String errorString;
	
	public ResponseError() {
		super(Action.CONNECT);
	}
	
	public ResponseError(Action action, int transactionId,
			String errorString)
	{
		super(action);
		this.setTransactionId(transactionId);
		this.errorString = errorString;
	}
	
	public byte[] getBytes() {
		int size = this.errorString.getBytes().length;
		ByteBuffer byteBuffer = ByteBuffer.allocate(16 + size);
		byteBuffer.order(ByteOrder.BIG_ENDIAN);
		
		byteBuffer.putInt(0, this.getAction().value());
		byteBuffer.putInt(8, this.getTransactionId());
		byteBuffer.putInt(12, size);
		byteBuffer.position(16);
		byteBuffer.put(this.errorString.getBytes());
		
		byteBuffer.flip();
		
		return byteBuffer.array();
	}
	
	public static ResponseError parse(byte[] byteArray) {
		ByteBuffer bufferReceive = ByteBuffer.wrap(byteArray);
		ResponseError request = new ResponseError();
		
		request.setAction(Action.valueOf(bufferReceive.getInt(0)));
		request.setTransactionId(bufferReceive.getInt(8));
		
		int stringLength = bufferReceive.getInt(12);
		byte[] stringByte = null;
		bufferReceive.get(stringByte, 16, stringLength);
		request.setErrorString(new String(stringByte));
		
		return request;
	}
	
	public void setErrorString(String errorString) {
		this.errorString = errorString;
	}
	
	public String getErrorString() {
		return this.errorString;
	}
}
