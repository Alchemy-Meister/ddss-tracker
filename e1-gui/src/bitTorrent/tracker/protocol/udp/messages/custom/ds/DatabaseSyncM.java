package bitTorrent.tracker.protocol.udp.messages.custom.ds;

import bitTorrent.tracker.protocol.udp.messages.custom.CustomMessage;
import bitTorrent.tracker.protocol.udp.messages.custom.Type;

/**
 * Offset  Size	 			Name   			Value
 * 0       32-bit integer	type   		    // 3, 4 or 5
 * 4       64-bit integer   connection_id   
 * @author Irene
 * @author Jesus
 */
public abstract class DatabaseSyncM extends CustomMessage {

	private static final long serialVersionUID = -2460534889358910251L;
	protected Type type;
	protected long connection_id;
	
	public DatabaseSyncM(Type type, long connection_id) {
		this.type = type;
		this.connection_id = connection_id;
	}
	
	@Override
	public Type getType() {
		return type;
	}

	public long getConnection_id() {
		return connection_id;
	}

	public void setConnection_id(long connection_id) {
		this.connection_id = connection_id;
	}
	
}
