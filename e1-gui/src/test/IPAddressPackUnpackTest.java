package test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import common.utils.Utilities;

public class IPAddressPackUnpackTest {

	public static void main(String [] args) throws UnknownHostException {
		int packed = Utilities.pack(InetAddress.getByName(
				"192.168.51.10").getAddress());
		System.out.println("Packed: " + packed); // -1062718710
		
		String unpacked = InetAddress.getByAddress(
				Utilities.unpack(packed)).getHostAddress();
		System.out.println(unpacked);
	}
}
