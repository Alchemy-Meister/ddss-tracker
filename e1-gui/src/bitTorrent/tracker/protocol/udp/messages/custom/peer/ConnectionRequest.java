package bitTorrent.tracker.protocol.udp.messages.custom.peer;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;

/**
*
* Offset  Size    			Name    			Value
* 0       64-bit integer  	connection_id
* 8       32-bit integer  	action          	1 // announce
* 12      32-bit integer  	transaction_id
* 
*/

public class ConnectionRequest {
	
	private static String CONNECTION_ID_STRING = "FEE1DEADBAADBEEF";
	
	private long connectionId;
	private int action, transactionId;
	private Random random = new Random();
	
	public ConnectionRequest() {
		this.connectionId = new BigInteger(
				CONNECTION_ID_STRING, 16).longValue();
		this.action = 0;
		this.transactionId = random.nextInt();
	}
	
	public byte[] getBytes() {
		ByteBuffer byteBuffer = ByteBuffer.allocate(16);
		byteBuffer.order(ByteOrder.BIG_ENDIAN);
		
		byteBuffer.putLong(0, this.connectionId);
		byteBuffer.putInt(8, this.action);
		byteBuffer.putInt(12, this.transactionId);
		
		byteBuffer.flip();
		
		return byteBuffer.array();
	}
	
	public static ConnectionRequest parse(byte[] byteArray) {
		ByteBuffer bufferReceive = ByteBuffer.wrap(byteArray);
		ConnectionRequest request = new ConnectionRequest();
		
		request.setConnectionId(bufferReceive.getLong(0));
		request.setAction(bufferReceive.getInt(8));
		request.setTransactionId(bufferReceive.getInt(12));
		
		return request;
	}
	
	public void setTransactionId(int transactionId) {
		this.transactionId = transactionId;
		
	}
	
	public int getTransactionId() {
		return this.transactionId;
	}

	public void setAction(int action) {
		this.action = action;
	}
	
	public int getAction() {
		return this.action;
	}

	public void setConnectionId(long connectionId) {
		this.connectionId = connectionId;
	}
	
	public long getConnectionId() {
		return this.connectionId;
	}

	@Override
	public String toString() {
		return "CR:\r\n\tConnection_id: " + this.connectionId +
				"\r\n\tAction: " + this.action +
				"\r\n\tTransaction_id: " + this.transactionId;
	}
	
	public static void main(String[] args) {
		ConnectionRequest a = new ConnectionRequest();
		System.out.println(a.toString());
	}
	
}