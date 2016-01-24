package tracker.controllers;

import common.utils.Utilities;
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
			return Utilities.isValidIP(ip);
		} else
			return true;
	}
	
	public boolean isValidMCIP(String ip) {
		if (Const.CHECK_INPUTS) {
			return Utilities.isValidMCIP(ip);
		} else
			return true;
		
	}

	public boolean isValidPort(String port) {
		if (Const.CHECK_INPUTS) {
			return Utilities.isValidPort(port);
		} else
			return true;
	}

	public void connect(int port, int peerPort, String ip) 
			throws NetProtoException {
		// TODO HERE IT BEGINS
		networker = Networker.getInstance(port, peerPort, ip);
		networker.startRW();
		FaultToleranceSys.setNetwork(ip, peerPort, port);
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