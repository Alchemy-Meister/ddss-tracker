package test;

import org.apache.commons.codec.digest.DigestUtils;

public class Sha1Test {

	public static void main(String [] args) {
		String helloWorld = "Hello world";
		System.out.println("--");
		byte [] hashBytes = DigestUtils.sha1(helloWorld);
		String bytes = "";
		for (byte i : hashBytes) {
			bytes += String.format("%02X", i);
		}
		System.out.println("bytes: " + bytes);
		System.out.println(hashBytes.toString());
		for (byte i : hashBytes)
			System.out.printf("0x%02X ", i);
		System.out.println();
		System.out.println("--");
		byte [] stringBytes = new String(bytes).getBytes();
		for (byte i : stringBytes)
			System.out.printf("0x%02X ", i);
		System.out.println();
		System.out.println("--");
		byte [] byteFromHex = hexStringToByteArray(bytes);
		for (byte i : byteFromHex)
			System.out.printf("0x%02X ", i);
		System.out.println();
		
	}
	
	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
}
