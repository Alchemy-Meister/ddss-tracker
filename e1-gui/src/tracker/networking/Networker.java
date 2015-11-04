package tracker.networking;

import javax.swing.JLabel;

/**
 * @author Irene
 * @author Jesus
 */
public class Networker implements InScheduler, OutScheduler {
	
	private static Networker instance = null;
	private Thread netStatusThread, networkerThread;
	private NetworkerRunnable netRunnable;
	private JLabel label;
	
	private Networker(int port, String ip, JLabel label) {
		
		this.label = label;
		this.netStatusThread = new Thread(new NetworkerStatusThread(label));
		this.netRunnable = new NetworkerRunnable(port, ip);
	}
	
	public static Networker getInstance(int port, String ip, JLabel label) {
		if (instance == null) {
			instance = new Networker(port, ip, label);
		}
		return instance;
	}
	
	public boolean isStatusThreadRunning() {
		return netStatusThread != null ? netStatusThread.isAlive() : false;
	}
	
	public void startStatusThread() {
		if (this.netStatusThread == null) {
			this.netStatusThread = new Thread(new NetworkerStatusThread(label));
		}
		if (!this.netStatusThread.isAlive()) {
			this.netStatusThread.start();
		}
	}
	
	public void stopStatusThread() {
		if (this.netStatusThread.isAlive()) {
			this.netStatusThread.interrupt();
			this.netStatusThread = null;
		}
	}
	
	public boolean isNetThreadRunning() {
		return networkerThread != null ? networkerThread.isAlive() : false;
	}
	
	public void startNetThread() {
		this.netRunnable.setNetworker(this);
		if (this.networkerThread == null)
			this.netStatusThread = new Thread(netRunnable);
		if (!this.networkerThread.isAlive())
			this.networkerThread.start();
	}
	
	public void stopNetThread() {
		if (this.networkerThread.isAlive()) {
			this.networkerThread.interrupt();
			this.networkerThread = null;
		}
	}
	
	@Override
	public void send(String param) {
		netRunnable.put(param);
		
	}

	@Override
	public void receive(String param) {
		// TODO Auto-generated method stub
		
	}
}
