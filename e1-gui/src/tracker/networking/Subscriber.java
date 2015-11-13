package tracker.networking;

import bitTorrent.tracker.protocol.udp.messages.custom.CustomMessage;

/** A class implementing this interface must first subscibe to the given topic
 * to receive updates.
 * @author Irene
 * @author Jesus
 */
public interface Subscriber {
	
	public void receive(Topic topic, CustomMessage param);
}
