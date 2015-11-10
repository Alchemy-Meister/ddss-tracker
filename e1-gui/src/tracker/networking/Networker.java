package tracker.networking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JLabel;

import tracker.exceptions.NetProtoException;
import tracker.networking.runnables.NetworkerReadRunnable;
import tracker.networking.runnables.NetworkerStatusRunnable;
import tracker.networking.runnables.NetworkerWriteRunnable;
import tracker.subsys.TrackerSubsystem;

/**
 * @author Irene
 * @author Jesus
 */
public class Networker implements Publisher {
	
	private static Networker instance = null;
	private Thread netStatusThread, netReadThread, netWriteThread;
	private NetworkerReadRunnable netReadRunnable;
	private NetworkerWriteRunnable netWriteRunnable;
	private NetworkerStatusRunnable statusRunnable;
	private HashMap<Topic, List<TrackerSubsystem>> subscribers;
	
	private Networker(int port, String ip) {
		this.netReadRunnable = new NetworkerReadRunnable(port, ip);
		this.netWriteRunnable = new NetworkerWriteRunnable(port, ip);
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
		//TODO
		return true;
	}
	
	public void startNetThread() {
		//TODO
		this.netReadRunnable.setNetworker(this);
		this.netWriteRunnable.setNetworker(this);
	}
	
	public void stopNetThread() {
		//TODO
	}
	
	public void startRW() throws NetProtoException {
		netReadRunnable.setNetworker(this);
		netReadRunnable.init();
		netWriteRunnable.setNetworker(this);
		netWriteRunnable.init();
		
		netReadThread = new Thread(netReadRunnable);
		netWriteThread = new Thread(netWriteRunnable);
		netReadThread.start();
		netWriteThread.start();
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
		netWriteRunnable.put(param);
	}

	/** Used by the networking thread. When a new notification enters, the
	 * networker redirects it to the subscribers of that notification.
	 * @param topic
	 * @param param
	 */
	public void notify(Topic topic, String param) {
		for (TrackerSubsystem subscriber : this.subscribers.get(topic))
			subscriber.receive(topic, param);
	}
}
