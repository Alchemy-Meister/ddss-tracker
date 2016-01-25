package tracker.db.model;

public class Peer {

	private int host;
	private short port;
	private int id;
	
	public Peer(int id, int host, short port) {
		this.host = host;
		this.port = port;
		this.id = id;
	}

	public int getHost() {
		return host;
	}

	public void setHost(int host) {
		this.host = host;
	}

	public short getPort() {
		return port;
	}

	public void setPort(short port) {
		this.port = port;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
}
