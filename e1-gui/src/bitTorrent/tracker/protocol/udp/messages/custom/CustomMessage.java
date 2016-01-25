package bitTorrent.tracker.protocol.udp.messages.custom;

import java.io.Serializable;

/**
 * Encapsulates all the custom messages.
 * @author Irene
 * @author Jesus
 */
public abstract class CustomMessage implements Serializable {
	private static final long serialVersionUID = 7978790252181283887L;
	public static final byte[] CRLF = {0x0A, 0x0D};

	public CustomMessage(){
	}

	public abstract byte[] getBytes();
	
	public abstract String toString();

	public abstract Type getType();
}
