package tracker.networking;

/** The classes implementing this interface have write access to the networking
 * thread. 
 * @author Irene
 * @author Jesus
 */
public interface Publisher {
	
	public void publish(Topic topic, String param);
	
}
