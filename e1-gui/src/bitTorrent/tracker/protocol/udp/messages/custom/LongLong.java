package bitTorrent.tracker.protocol.udp.messages.custom;

import java.math.BigInteger;

/**
 * Represents a 128-bit unsigned long
 * @author Irene
 * @author Jesus
 */
public class LongLong {

	private BigInteger value = null; // just to print
	private byte[] mostLeft, leastRight; // these hold the real value

	private void setValue(byte[] mostLeft, byte[] leastRight) {
		// 0x00 0x00 0x00 0x01 -> ONE
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

	public LongLong(byte[] inBytes) {
		byte[] mostLeft = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
		byte[] leastRight = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
		int cursor = 15;
		for (int i = inBytes.length - 1; i >= 0; i--) {
			if (cursor >= 8)
				leastRight[cursor - 8] = inBytes[i];
			else
				mostLeft[cursor] = inBytes[i];
			cursor--;
		}
		this.setValue(mostLeft, leastRight);
		this.mostLeft = mostLeft;
		this.leastRight = leastRight;
	}

	public byte[] getBytes() {
		byte[] ret = new byte[16];
		System.arraycopy(this.mostLeft, 0, ret, 0, this.mostLeft.length);
		System.arraycopy(this.leastRight, 0, ret, this.mostLeft.length,
				this.leastRight.length);
		return ret;
	}

	@Override
	public String toString() {
		return "LongLong [value=" + value + "]";
	}
}
