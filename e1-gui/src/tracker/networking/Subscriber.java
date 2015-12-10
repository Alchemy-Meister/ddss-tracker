package tracker.networking;

/** A class implementing this interface must first subscibe to the given topic
 * to receive updates.
 * @author Irene
 * @author Jesus
 */
public interface Subscriber {
	
	public void receive(Topic topic, Bundle bundle);
}
