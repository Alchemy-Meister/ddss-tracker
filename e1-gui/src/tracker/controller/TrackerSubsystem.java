package tracker.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;


/** Base class implemented by all the tracker's subsystems.
 * @author Irene
 * @author Jesus
 */
public abstract class TrackerSubsystem {

	protected List<Observer> observers = null;
	
	public TrackerSubsystem() {
		observers = new ArrayList<Observer>();
	}
	
	protected void addObserver(Observer o) {
		this.observers.add(o);
	}
	
	protected void removeObserver(Observer o) {
		this.observers.remove(o);
	}
	
	public void notifyObservers(Object param) {
		for (Observer o : this.observers)
			if (o != null)
				o.update(null, param);
	}
	
}
