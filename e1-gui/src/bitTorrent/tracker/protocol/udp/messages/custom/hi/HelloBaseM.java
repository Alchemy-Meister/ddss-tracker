package bitTorrent.tracker.protocol.udp.messages.custom.hi;

import bitTorrent.tracker.protocol.udp.messages.custom.CustomMessage;
import bitTorrent.tracker.protocol.udp.messages.custom.Type;

/**
 * Offset  Size	 			Name   			Value
 * 0       32-bit integer	type   		    2 // hi message
 * 4       64-bit integer   connection_id   
 * @author Irene
 * @author Jesus
 */
public abstract class HelloBaseM extends CustomMessage {

	protected final Type type = Type.HI;
	protected long connection_id;
	
	public HelloBaseM(long connection_id) {
		this.connection_id = connection_id;
	}
	
	public long getConnection_id() {
		return connection_id;
	}
	
	public void setConnection_id(long connection_id) {
		this.connection_id = connection_id;
	}
	
	@Override
	public Type getType() {
		return type;
	}
	
}
