package bitTorrent.tracker.protocol.udp.messages.custom;

import java.math.BigInteger;

/**
 * Represents a 128-bit unsigned long
 * @author Irene
 * @author Jesus
 */
public class LongLong { // TODO wrong order

	private BigInteger value = null;
	
	private void setValue(byte[] mostLeft, byte[] leastRight) {
		if (mostLeft.length > 8 || leastRight.length > 8) {
			// TODO throw error
		} else {
			byte[] ret = new byte[mostLeft.length + leastRight.length];
			System.arraycopy(mostLeft, 0, ret, 0, mostLeft.length);
			System.arraycopy(leastRight, 0, ret, mostLeft.length,
					leastRight.length);
			this.value = new BigInteger(ret);
		}
	}
	
	public LongLong(String valueRep) {
		this(new BigInteger(valueRep).toByteArray());
	}
	
	public LongLong(byte[] mostLeft, byte[] leastRight) {
		this.setValue(mostLeft, leastRight);
	}
	
	public LongLong(byte[] bytes) {
		for (byte  i : bytes) {
			System.out.printf("0x%02X ", i);
		}
		byte[] mostLeft = new byte[8];
		byte[] leastRight = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
		if (bytes.length < 8) {
			System.arraycopy(bytes, 0, mostLeft, 0, bytes.length);
			for (int i = bytes.length; i < 8; i++)
				mostLeft[i] = 0x00;
		} else {
			System.arraycopy(bytes, 0, mostLeft, 0, 8);
			System.arraycopy(bytes, 8, leastRight, 0, bytes.length - 8);
		}
		this.setValue(mostLeft, leastRight);
	}
	
	public byte[] getBytes() {
		byte[] ret = new byte[16];
		byte[] valueBytes = this.value.toByteArray();
		System.arraycopy(valueBytes, 0, ret, 0, valueBytes.length);
		if (valueBytes.length < 16) {
			for (int i = valueBytes.length; i < 16; i++)
				ret[i] = 0x00;
		}
		return ret;
	}

	@Override
	public String toString() {
		return "LongLong [value=" + value + "]";
	}
}
