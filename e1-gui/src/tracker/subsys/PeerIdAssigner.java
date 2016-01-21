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
		if (highest == null) {
			byte[] bytes = new byte[16];
			new SecureRandom().engineNextBytes(bytes);
			BigInteger rett = new BigInteger(bytes);
			if (rett.signum() != 1) { // we don't like negative ids 
				rett = new BigInteger(rett.multiply(
						new BigInteger("-1")).toByteArray());
			}
			return new LongLong(rett.toByteArray());
		} else {
			BigInteger bHighest = new BigInteger(highest.getBytes());
			return new LongLong(bHighest.add(new BigInteger("1")).toByteArray());
		}
	}
	
}
