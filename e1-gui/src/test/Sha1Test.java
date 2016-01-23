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
	}
}
