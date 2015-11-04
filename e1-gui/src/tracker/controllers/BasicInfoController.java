package tracker.controllers;

import java.util.regex.Pattern;

import javax.swing.JLabel;

import tracker.networking.Networker;

public class BasicInfoController {
	
	private Networker networker;
	
	public BasicInfoController() {
		// TODO start whatever dependecy goes here.
	}
	
	public boolean isValidIP(String ip) {
		String pattern = "^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$";
		Pattern r = Pattern.compile(pattern);
		return r.matcher(ip).find();
	}
	
	public boolean isValidPort(String port) {
		try {
			int portNo = Integer.parseInt(port);
			return portNo > 1023 && portNo <= 65535;
		} catch(NumberFormatException e) {
			return false;
		}
	}
	
	public void connect(int port, String ip, JLabel label) {
		networker = Networker.getInstance(port, ip, label);
		networker.startStatusThread();
	}
	
	public void disconnect() {
		networker.stopStatusThread();
	}
	
	public boolean isConnected() {
		if(networker == null) {
			System.out.println(networker);
			return false;
		} else {
			System.out.println(networker.isStatusThreadRunning());
			return networker.isStatusThreadRunning();
		}
	}
}