package tracker.networking;

import bitTorrent.tracker.protocol.udp.messages.custom.CustomMessage;

/** The classes implementing this interface have write access to the networking
 * thread. 
 * @author Irene
 * @author Jesus
 */
public interface Publisher {
	
	public void publish(Topic topic, CustomMessage param);
	
}
