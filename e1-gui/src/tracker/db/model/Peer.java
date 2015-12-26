package tracker.db.model;

public class Peer {

	private int host;
	private short port;
	
	public Peer(int host, short port) {
		this.host = host;
		this.port = port;
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
}
