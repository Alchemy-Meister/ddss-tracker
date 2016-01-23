package bitTorrent.tracker.protocol.udp.messages.custom.peer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
*
* Offset  Size    			Name    			Value
* 0   	  32-bit integer  	action          	
* 8       32-bit integer  	transaction_id
* 12	  32-bit integer	stringLength
* 16	  stringLength 		errorString
* 
*/

public class ConnectionResponseError {
	
	private String errorString;
	private int action, transactionId;
	
	public byte[] getBytes() {
		int size = this.errorString.getBytes().length;
		ByteBuffer byteBuffer = ByteBuffer.allocate(16 + size);
		byteBuffer.order(ByteOrder.BIG_ENDIAN);
		
		byteBuffer.putInt(0, this.action);
		byteBuffer.putInt(8, this.transactionId);
		byteBuffer.putInt(12, size);
		byteBuffer.position(16);
		byteBuffer.put(this.errorString.getBytes());
		
		byteBuffer.flip();
		
		return byteBuffer.array();
	}
	
	public static ConnectionResponseError parse(byte[] byteArray) {
		ByteBuffer bufferReceive = ByteBuffer.wrap(byteArray);
		ConnectionResponseError request = new ConnectionResponseError();
		
		request.setAction(bufferReceive.getInt(0));
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
	
	public void setAction(int action) {
		this.action = action;
	}
	
	public int getAction() {
		return this.action;
	}
	
	public void setTransactionId(int transactionID) {
		this.transactionId = transactionID;
	}
	
	public int getTransactionId() {
		return this.transactionId;
	}
}
