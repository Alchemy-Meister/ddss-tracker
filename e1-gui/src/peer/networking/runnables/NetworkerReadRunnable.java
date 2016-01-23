package peer.networking.runnables;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

import peer.networking.Networker;

public class NetworkerReadRunnable implements Runnable {
	private int port;
	private String ip;
	private Networker networker = null;
	private MulticastSocket socket;
	private InetAddress group;
	private boolean initialized = false;
	
	public NetworkerReadRunnable(int port, String ip, Networker networker) {
		this.port = port;
		this.ip = ip;
		this.networker = networker;
	}

	public void init() throws SocketException {
		try {
			this.group = InetAddress.getByName(ip);
			MulticastSocket socket = new MulticastSocket(port);
			this.socket = socket; // TODO check
			socket.joinGroup(this.group);
			this.initialized = true;
		} catch (UnknownHostException | SocketException e) {
			throw new SocketException();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		if(this.initialized) {
			while(!Thread.currentThread().isInterrupted()) {
				System.out.println("Read running!!!");
			}
		}
	}
}
