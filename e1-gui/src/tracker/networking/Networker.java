package tracker.networking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JLabel;

import tracker.subsys.TrackerSubsystem;

/**
 * @author Irene
 * @author Jesus
 */
public class Networker implements Publisher {
	
	private static Networker instance = null;
	private Thread netStatusThread, networkerThread;
	private NetworkerRunnable netRunnable;
	private NetworkerStatusRunnable statusRunnable;
	private HashMap<Topic, List<TrackerSubsystem>> subscribers;
	
	private Networker(int port, String ip) {
		this.netRunnable = new NetworkerRunnable(port, ip);
		this.statusRunnable = new NetworkerStatusRunnable();
		this.subscribers = new HashMap<Topic, List<TrackerSubsystem>>();
	}
	
	public static Networker getInstance(int port, String ip) {
		if (instance == null) {
			instance = new Networker(port, ip);
		}
		return instance;
	}
	
	public boolean isStatusThreadRunning() {
		return netStatusThread != null ? netStatusThread.isAlive() : false;
	}
	
	public void startStatusThread(JLabel statusLabel) {
		if (this.netStatusThread == null) {
			this.statusRunnable.setStatusLabel(statusLabel);
			this.netStatusThread = new Thread(this.statusRunnable);
		}
		
		if (!this.netStatusThread.isAlive()) {
			this.statusRunnable.setStatusLabel(statusLabel);
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
			this.networkerThread = new Thread(netRunnable);
		if (!this.networkerThread.isAlive())
			this.networkerThread.start();
	}
	
	public void stopNetThread() {
		if (this.networkerThread.isAlive()) {
			this.networkerThread.interrupt();
			this.networkerThread = null;
		}
	}
	
	/** Requests a subscrition to the given topic by the given susbsystem.
	 * @param topic
	 * @param subsystem
	 */
	public void subscribe(Topic topic, TrackerSubsystem subsystem) {
		if (subscribers.get(topic) == null)
			subscribers.put(topic, new ArrayList<TrackerSubsystem>());
		subscribers.get(topic).add(subsystem);
	}

	@Override
	public void publish(Topic topic, String param) {
		netRunnable.put(param);
	}

	/** Used by the networking thread. When a new notification enters, the
	 * networker redirects it to the subscribers of that notification.
	 * @param topic
	 * @param param
	 */
	protected void notify(Topic topic, String param) {
		for (TrackerSubsystem subscriber : this.subscribers.get(topic))
			subscriber.receive(topic, param);
	}
}
