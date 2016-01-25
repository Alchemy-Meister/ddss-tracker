package test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import common.utils.Utilities;

public class IntegerTest {

	public static void main(String [] args) throws UnknownHostException {
		int packed = Utilities.pack(InetAddress.getByName(
				"192.168.51.10").getAddress());
		System.out.println("init: " + packed);
		byte[] hostBytes = ByteBuffer.allocate(4).putInt(packed).array();
		ByteBuffer messageBytes = ByteBuffer.wrap(hostBytes);
		int then = messageBytes.getInt(0);
		System.out.println("then: " + then);
	}
}
