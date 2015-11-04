package tracker.networking;

import javax.swing.JLabel;

public class Networker implements InScheduler, OutScheduler {
	
	private static Networker instance = null;
	private Thread networkerThread;
	private JLabel label;
	
	private Networker(int port, JLabel label) {
		
		this.label = label;
		this.networkerThread= new Thread(new NetworkerThread(label));
	}
	
	public static Networker getInstance(int port, JLabel label) {
		if(instance == null) {
			instance = new Networker(port, label);
		}
		return instance;
	}
	
	public boolean isRunning() {
		if(networkerThread != null) {
			return networkerThread.isAlive();
		} else {
			return false;
		}
	}
	
	public void start() {
		if(this.networkerThread == null) {
			this.networkerThread = new Thread(new NetworkerThread(label));
		}
		if(!this.networkerThread.isAlive()) {
			this.networkerThread.start();
		}
	}
	
	public void stop() {
		if(this.networkerThread.isAlive()) {
			this.networkerThread.interrupt();
			this.networkerThread = null;
		}
	}
	
	@Override
	public void send() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receive() {
		// TODO Auto-generated method stub
		
	}
}
