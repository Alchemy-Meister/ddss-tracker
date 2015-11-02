package bitTorrent.tracker.protocol.udp.messages.custom.ds;

import bitTorrent.tracker.protocol.udp.messages.custom.Type;

/**
 * Offset  Size	 			Name   			Value
 * 0       32-bit integer	type   			3 // ds-ready message
 * 4       32-bit integer   connection_id 
 * @author Irene
 * @author Jesus
 *
 */
public class DSReadyM extends DatabaseSyncM {

	public DSReadyM(int connection_id) {
		super(Type.DS_READY, connection_id);
	}

}
