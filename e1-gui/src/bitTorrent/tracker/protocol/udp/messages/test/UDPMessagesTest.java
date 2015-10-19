package bitTorrent.tracker.protocol.udp.messages.test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;

import bitTorrent.tracker.protocol.udp.messages.BitTorrentUDPMessage.Action;
import bitTorrent.tracker.protocol.udp.messages.ConnectRequest;

public class UDPMessagesTest {
	
	public static void main(String[] args) {
		
		//Create connect request message
		ConnectRequest msgSend = new ConnectRequest(); 
		
		//TransactionID is a random 32-bit int
		msgSend.setTransactionId(new Random().nextInt(Integer.MAX_VALUE));
		
		//Create a 16-byte buffer to allocate the message
		ByteBuffer bufferSend = ByteBuffer.allocate(16);
		//Set byte order to BigEndian
		bufferSend.order(ByteOrder.BIG_ENDIAN);
		
		//Put the fields of the message in the byte array
		bufferSend.putLong(0, msgSend.getConnectionId());		//index  0 - 64-bit integer connection_id
		bufferSend.putInt(8, msgSend.getAction().value());		//index  8 - 32-bit integer action
		bufferSend.putInt(12, msgSend.getTransactionId());		//index 12 - 32-bit integer transaction_id
		
		//Set the index back to 0 and prepare the buffer to 'get' operations
		bufferSend.flip();
		
		//Convert the buffer to an array of bytes
		byte[] msgBytes = bufferSend.array();
				
		//Create a new connect request message
		ConnectRequest msgReceive = new ConnectRequest();
		
		//Create a buffer with the previously array of bytes
		ByteBuffer bufferReceive = ByteBuffer.wrap(msgBytes);
		
		//Get the fields by reading the buffer usinf index and data type
		msgReceive.setConnectionId(bufferReceive.getLong(0));				//index  0 - 64-bit integer connection_id
		msgReceive.setAction(Action.valueOf(bufferReceive.getInt(8)));		//index  8 - 32-bit integer action
		msgReceive.setTransactionId(bufferReceive.getInt(12));				//index 12 - 32-bit integer transaction_id
		
		//Print sent and received messages
		System.out.println("-> connection_id:" + msgSend.getConnectionId() + " - action:" + msgSend.getAction() + " - transaction_id:" + msgSend.getTransactionId());
		System.out.println("<- connection_id:" + msgSend.getConnectionId() + " - action:" + msgSend.getAction() + " - transaction_id:" + msgSend.getTransactionId());
		
		System.out.print("\n- byte[]: ");
		
		for (byte b : msgBytes) {
			System.out.print(b + ", ");
		}
		
		System.out.println();
	}	
}