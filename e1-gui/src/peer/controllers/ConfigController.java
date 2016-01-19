package peer.controllers;

import java.net.SocketException;

import common.utils.Utilities;
import peer.networking.Networker;

public class ConfigController {
	
	private Networker networker;
	
	public boolean isValidIP(String ip) {
		return Utilities.isValidIP(ip);
	}
	
	public boolean isValidMCIP(String ip) {
		return Utilities.isValidMCIP(ip);
	}
	
	public boolean isValidPort(String port) {
		return Utilities.isValidPort(port);
	}
	
	public boolean isConnected() {
		if(networker != null) {
			return networker.isNetThreadRunning();
		} else {
			return false;
		}
	}
	
	public void connect(int port, String ip) throws SocketException {
		networker = Networker.getInstance(port, ip);
		networker.startRW();
	}
	
	public void disconnect() {
		networker.stopNetThread();
	}
}
