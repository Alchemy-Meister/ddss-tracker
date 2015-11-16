package bitTorrent.tracker.protocol.udp.messages.custom.ds;

import bitTorrent.tracker.protocol.udp.messages.custom.LongLong;
import bitTorrent.tracker.protocol.udp.messages.custom.Type;

/**
 * Offset  Size	 			Name   			Value
 * 0       32-bit integer	type   			5 // ds-done message
 * 4       64-bit integer   connection_id
 * @author Irene
 * @author Jesus
 *
 */
public class DSDoneM extends DatabaseSyncM {

	public DSDoneM(LongLong connection_id) {
		super(Type.DS_DONE, connection_id);
	}

	@Override
	public byte[] getBytes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

}
