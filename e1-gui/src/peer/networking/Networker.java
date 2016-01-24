package peer.networking;

import java.net.DatagramSocket;
import java.net.SocketException;

import peer.networking.runnables.NetworkerReadRunnable;
import peer.networking.runnables.NetworkerWriteRunnable;

public class Networker {
	private static Networker instance = null;
	private Thread netReadThread, netWriteThread;
	private NetworkerReadRunnable netReadRunnable;
	private NetworkerWriteRunnable netWriteRunnable;
	
	private Networker(int port, String ip) {
		try {
			DatagramSocket socket = new DatagramSocket();
			this.netReadRunnable = new NetworkerReadRunnable(socket);
			this.netWriteRunnable = new NetworkerWriteRunnable(port, ip, socket);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public static Networker getInstance(int port, String ip) {
		if (instance == null) {
			instance = new Networker(port, ip);
		} else {
			instance.netWriteRunnable.setIP(ip);
			instance.netWriteRunnable.setPort(port);
		}
		return instance;
	}
	
	public boolean isNetThreadRunning() {
		return (this.netReadThread != null && this.netReadThread.isAlive()) ||
				(this.netWriteThread != null && this.netWriteThread.isAlive());
	}
	
	public void startRW() throws SocketException {
		netReadRunnable.setNetworker(this);
		netWriteRunnable.setNetworker(this);
		netWriteRunnable.init();
		
		netReadThread = new Thread(netReadRunnable);
		netWriteThread = new Thread(netWriteRunnable);
		netReadThread.start();
		netWriteThread.start();
	}
	
	public void stopNetThread() {
		if(this.netReadThread != null && this.netReadThread.isAlive()) {
			this.netReadThread.interrupt();
			this.netReadRunnable.interrupt();
		}
		if(this.netWriteThread != null && this.netWriteThread.isAlive()) {
			this.netWriteThread.interrupt();
		}
	}
	
	public void receivedConnectionMessage() {
		this.netWriteRunnable.cResponseReceived();
	}
	
	public void setConnectionId(long connectionId) {
		this.netWriteRunnable.setConnectionID(connectionId);
	}
}
