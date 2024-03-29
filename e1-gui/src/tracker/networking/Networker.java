package tracker.networking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bitTorrent.tracker.protocol.udp.messages.custom.CustomMessage;
import tracker.exceptions.NetProtoException;
import tracker.networking.runnables.NetworkerPeerReadRunnable;
import tracker.networking.runnables.NetworkerReadRunnable;
import tracker.networking.runnables.NetworkerWriteRunnable;
import tracker.subsys.TrackerSubsystem;

/**
 * @author Irene
 * @author Jesus
 */
public class Networker implements Publisher {

	private static Networker instance = null;
	private Dispatcher dispatcher = null; // needed to jms
	private Thread netReadThread, netWriteThread, netPeerReadThread;
	private NetworkerReadRunnable netReadRunnable;
	private NetworkerPeerReadRunnable netPeerRunnable;
	private NetworkerWriteRunnable netWriteRunnable;
	private HashMap<Topic, List<TrackerSubsystem>> subscribers;

	private Networker(int port, int peerPort, String ip) {
		this.netReadRunnable = new NetworkerReadRunnable(port, ip);
		this.netWriteRunnable = new NetworkerWriteRunnable(port, ip);
		this.netPeerRunnable = new NetworkerPeerReadRunnable(peerPort, ip);
		this.subscribers = new HashMap<Topic, List<TrackerSubsystem>>();
	}

	public static Networker getInstance(int port, int peerPort, String ip) {
		if (instance == null) {
			instance = new Networker(port, peerPort, ip);
		} else {
			instance.netReadRunnable.setIP(ip);
			instance.netReadRunnable.setPort(port);
			instance.netWriteRunnable.setIP(ip);
			instance.netWriteRunnable.setPort(port);
			instance.netPeerRunnable.setIP(ip);
			instance.netPeerRunnable.setPort(peerPort);
		}
		return instance;
	}
	
	
	public void setDispatcher(Dispatcher d) {
		this.dispatcher = d;
		netPeerRunnable.setDispatcher(this.dispatcher);
	}

	public boolean isNetThreadRunning() {
		return (this.netReadThread != null && this.netReadThread.isAlive()) 
				|| (this.netWriteThread != null && this.netWriteThread.isAlive())
				|| (this.netPeerReadThread != null && 
				this.netPeerReadThread.isAlive());
	}

	public void stopNetThread() {
		if(this.netReadThread != null && this.netReadThread.isAlive()) {
			this.netReadThread.interrupt();
			this.netReadRunnable.interrupt();
		}
		if(this.netWriteThread != null && this.netWriteThread.isAlive()) {
			this.netWriteThread.interrupt();
		}
		if(this.netPeerReadThread != null && this.netPeerReadThread.isAlive()) {
			this.netPeerReadThread.interrupt();
			this.netPeerRunnable.interrupt();
		}

	}

	public void startRW() throws NetProtoException {
		netReadRunnable.setNetworker(this);
		netReadRunnable.init();
		netWriteRunnable.setNetworker(this);
		netWriteRunnable.init();
		netPeerRunnable.setNetworker(this);
		netPeerRunnable.init();
		

		netReadThread = new Thread(netReadRunnable);
		netWriteThread = new Thread(netWriteRunnable);
		netPeerReadThread = new Thread(netPeerRunnable);
		netReadThread.start();
		netWriteThread.start();
		netPeerReadThread.start();
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
	public void publish(Topic topic, CustomMessage param) {
		netWriteRunnable.put(param);
	}

	/** Used by the networking thread. When a new notification enters, the
	 * networker redirects it to the subscribers of that notification.
	 * @param topic
	 * @param param
	 */
	public void notify(Topic topic, Bundle bundle) {
		if (this.subscribers.get(topic) != null) {
			for (TrackerSubsystem subscriber : this.subscribers.get(topic))
				subscriber.receive(topic, bundle);
		}
	}
}
