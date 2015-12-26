package tracker.subsys;

import java.math.BigInteger;

import bitTorrent.tracker.protocol.udp.messages.custom.LongLong;
import sun.security.provider.SecureRandom;
import tracker.subsys.cfts.IpIdTable;

/**
 * Assigns ids to the new slaves.
 * @author Irene
 * @author Jesus
 */
public class PeerIdAssigner {

	public static LongLong assignId() {
		IpIdTable ipidTable = IpIdTable.getInstance();
		LongLong highest = ipidTable.getHighestId();
		byte[] bytes = new byte[16];
		new SecureRandom().engineNextBytes(bytes);
		BigInteger rett = new BigInteger(bytes);
		LongLong ret = new LongLong(rett.toByteArray());
		if (rett.signum() == -1)
			rett = rett.multiply(new BigInteger("-1"));
		if (highest != null) {
			BigInteger another = rett.add(new BigInteger(highest.getBytes()));
			ret = new LongLong(another.toByteArray());
		}
		return ret;
	}
	
}
