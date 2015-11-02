package bitTorrent.tracker.protocol.udp.messages.custom.ds;

import bitTorrent.tracker.protocol.udp.messages.custom.Type;

/**
 * Offset  Size	 			Name   			Value
 * 0       32-bit integer	type   			5 // ds-done message
 * 4       32-bit integer   connection_id 
 * @author Irene
 * @author Jesus
 *
 */
public class DSDoneM extends DatabaseSyncM {

	public DSDoneM(int connection_id) {
		super(Type.DS_DONE, connection_id);
	}

}
