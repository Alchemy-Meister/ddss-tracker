package bitTorrent.tracker.protocol.udp.messages.custom.ds;

import bitTorrent.tracker.protocol.udp.messages.custom.CustomMessage;
import bitTorrent.tracker.protocol.udp.messages.custom.LongLong;
import bitTorrent.tracker.protocol.udp.messages.custom.Type;

public abstract class DatabaseSyncM extends CustomMessage {

	protected Type type;
	protected LongLong connection_id;
	
	public DatabaseSyncM(Type type, LongLong connection_id) {
		this.type = type;
		this.connection_id = connection_id;
	}
	
	public Type getType() {
		return type;
	}

	public LongLong getConnection_id() {
		return connection_id;
	}

	public void setConnection_id(LongLong connection_id) {
		this.connection_id = connection_id;
	}
	
}
