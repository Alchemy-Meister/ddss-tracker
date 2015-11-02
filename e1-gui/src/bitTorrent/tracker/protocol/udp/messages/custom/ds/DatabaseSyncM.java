package bitTorrent.tracker.protocol.udp.messages.custom.ds;

import bitTorrent.tracker.protocol.udp.messages.custom.Type;

public abstract class DatabaseSyncM {

	private Type type;
	private int connection_id;
	
	public DatabaseSyncM(Type type, int connection_id) {
		this.type = type;
		this.connection_id = connection_id;
	}
	
	public Type getType() {
		return type;
	}

	public int getConnection_id() {
		return connection_id;
	}

	public void setConnection_id(int connection_id) {
		this.connection_id = connection_id;
	}
	
}
