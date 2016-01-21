package common.utils;

import java.util.regex.Pattern;

public class Utilities {
	public static boolean isValidIP(String ip) {
		String pattern = "^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$";
		Pattern r = Pattern.compile(pattern);
		return r.matcher(ip).find();
	}
	
	public static boolean isValidMCIP(String ip) {
		String pattern = "2(?:2[4-9]|3\\d)(?:\\.(?:25[0-5]|2[0-4]" 
				+ "\\d|1\\d\\d|[1-9]\\d?|0)){3}$";
		Pattern r = Pattern.compile(pattern);
		return r.matcher(ip).find();
	}
	
	public static boolean isValidPort(String port) {
		try {
			int portNo = Integer.parseInt(port);
			return portNo > 1023 && portNo <= 65535;
		} catch(NumberFormatException e) {
			return false;
		}
	}
}