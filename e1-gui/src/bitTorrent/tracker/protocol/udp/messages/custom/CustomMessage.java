package bitTorrent.tracker.protocol.udp.messages.custom;

/**
 * Encapsulates all the custom messages.
 * @author Irene
 * @author Jesus
 */
public abstract class CustomMessage {

	public CustomMessage(){
		
	}

	public abstract byte[] getBytes();
	
	public abstract String toString();

}
