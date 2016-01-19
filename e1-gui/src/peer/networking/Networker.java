package peer.networking;

import java.net.SocketException;

import peer.networking.runnables.NetworkerReadRunnable;
import peer.networking.runnables.NetworkerWriteRunnable;

public class Networker {
	private static Networker instance = null;
	private Thread netReadThread, netWriteThread;
	private NetworkerReadRunnable netReadRunnable;
	private NetworkerWriteRunnable netWriteRunnable;
	
	private Networker(int port, String ip) {
		this.netReadRunnable = new NetworkerReadRunnable(port, ip, instance);
		this.netWriteRunnable = new NetworkerWriteRunnable(port, ip, instance);
	}
	
	public static Networker getInstance(int port, String ip) {
		if(instance != null) {
			return instance;
		} else {
			return new Networker(port, ip);
		}
	}
	
	public boolean isNetThreadRunning() {
		return (this.netReadThread != null && this.netReadThread.isAlive()) ||
				(this.netWriteThread != null && this.netWriteThread.isAlive());
	}
	
	public void startRW() throws SocketException {
		netReadRunnable.init();
		netWriteRunnable.init();
		
		netReadThread = new Thread(netReadRunnable);
		netWriteThread = new Thread(netWriteRunnable);
		netReadThread.start();
		netWriteThread.start();
	}
	
	public void stopNetThread() {
		if(this.netReadThread != null && this.netReadThread.isAlive()) {
			this.netReadThread.interrupt();
			// TODO CHECK
			// this.netReadRunnable.interrupt();
		}
		if(this.netWriteThread != null && this.netWriteThread.isAlive()) {
			this.netWriteThread.interrupt();
		}
	}
}
