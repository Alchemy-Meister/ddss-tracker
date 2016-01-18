package tracker.controllers;

import java.util.regex.Pattern;

import tracker.Const;
import tracker.exceptions.NetProtoException;
import tracker.networking.Networker;
import tracker.subsys.cfts.FaultToleranceSys;

public class BasicInfoController {

	private Networker networker;
	private FaultToleranceSys fts;

	public BasicInfoController() {
		// TODO start whatever dependecy goes here.
	}

	public boolean isValidIP(String ip) {
		if (Const.CHECK_INPUTS) {
			String pattern = "^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$";
			Pattern r = Pattern.compile(pattern);
			return r.matcher(ip).find();
		} else
			return true;
	}
	
	public boolean isValidMCIP(String ip) {
		if (Const.CHECK_INPUTS) {
			String pattern = "2(?:2[4-9]|3\\d)(?:\\.(?:25[0-5]|2[0-4]" 
					+ "\\d|1\\d\\d|[1-9]\\d?|0)){3}";
			Pattern r = Pattern.compile(pattern);
			return r.matcher(ip).find();
		} else
			return true;
		
	}

	public boolean isValidPort(String port) {
		if (Const.CHECK_INPUTS) {
			try {
				int portNo = Integer.parseInt(port);
				return portNo > 1023 && portNo <= 65535;
			} catch(NumberFormatException e) {
				return false;
			}
		} else
			return true;
	}

	public void connect(int port, String ip) 
			throws NetProtoException {
		// TODO HERE IT BEGINS
		networker = Networker.getInstance(port, ip);
		networker.startRW();
		FaultToleranceSys.setNetwork(ip, port);
		fts = FaultToleranceSys.getInstance();
		fts.run();
	}

	public void disconnect() {
		networker.stopNetThread();
		fts.stop();
	}

	public boolean isConnected() {
		if(networker == null) 
			return false;
		return networker.isNetThreadRunning();
	}
}