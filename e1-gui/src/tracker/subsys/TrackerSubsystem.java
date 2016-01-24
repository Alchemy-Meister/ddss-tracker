package tracker.subsys;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import tracker.networking.Networker;
import tracker.networking.Subscriber;


/** Base class implemented by all the tracker's subsystems.
 * @author Irene
 * @author Jesus
 */
public abstract class TrackerSubsystem implements Subscriber {

	protected List<Observer> observers = null;
	protected static Networker networker;
	
	public TrackerSubsystem() {
		observers = new ArrayList<Observer>();
	}
	
	public void addObserver(Observer o) {
		this.observers.add(o);
	}
	
	public void removeObserver(Observer o) {
		this.observers.remove(o);
	}
	
	public void notifyObservers(Object param) {
		for (Observer o : this.observers)
			if (o != null)
				o.update(null, param);
	}
	
	public static void setNetwork(String ip, int port, int peerPort) {
		if (networker == null)
			networker = Networker.getInstance(port, peerPort, ip);
	}
	
}
