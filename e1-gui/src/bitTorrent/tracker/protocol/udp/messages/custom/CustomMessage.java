package bitTorrent.tracker.protocol.udp.messages.custom;

/**
 * Encapsulates all the custom messages.
 * @author Irene
 * @author Jesus
 */
public abstract class CustomMessage {

	public static final byte[] CRLF = {0x0A, 0x0D};

	public CustomMessage(){
	}

	public abstract byte[] getBytes();
	
	public abstract String toString();

	public abstract Type getType();
}
