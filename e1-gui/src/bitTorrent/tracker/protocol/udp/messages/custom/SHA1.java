package bitTorrent.tracker.protocol.udp.messages.custom;

/**
 * 20 byte class to hold SHA1
 *
 */
public class SHA1 {

	private byte[] sha1;
	
	public SHA1(byte[] sha1) throws Exception {
		if (sha1.length != 20)
			throw new Exception("Invalid SHA1");
		else {
			this.sha1 = new byte[20];
			System.arraycopy(sha1, 0, this.sha1, 0, 20);
		}
	}
	
	public byte[] getSHA1() {
		return this.sha1;
	}
	
	public void setSHA1(byte[] sha1) throws Exception {
		if (sha1.length != 20)
			throw new Exception("Invalid SHA1");
		else {
			this.sha1 = new byte[20];
			System.arraycopy(sha1, 0, this.sha1, 0, 20);
		}
	}
	
	public byte[] getBytes() {
		return this.sha1;
	}
	

	@Override
	public String toString() {
		String bytes = "";
		for (byte i : sha1) {
			bytes += String.format("%02X", i);
		}
		return bytes;
	}
}
