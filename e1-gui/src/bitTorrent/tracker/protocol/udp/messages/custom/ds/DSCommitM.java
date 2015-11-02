package bitTorrent.tracker.protocol.udp.messages.custom.ds;

import bitTorrent.tracker.protocol.udp.messages.custom.Type;

/**
 * Offset  Size	 			Name   			Value
 * 0       32-bit integer	type   			4 // ds-commit message
 * 4       32-bit integer   connection_id 
 * @author Irene
 * @author Jesus
 *
 */
public class DSCommitM extends DatabaseSyncM {

	public DSCommitM(int connection_id) {
		super(Type.DS_COMMIT, connection_id);
	}

}
