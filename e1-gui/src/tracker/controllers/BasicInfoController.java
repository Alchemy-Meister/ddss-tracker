package tracker.controllers;

import common.utils.Utilities;
import tracker.Const;
import tracker.exceptions.NetProtoException;
import tracker.networking.Dispatcher;
import tracker.networking.Networker;
import tracker.subsys.cfts.FaultToleranceSys;
import tracker.subsys.db.DBFaultToleranceSys;

public class BasicInfoController {

	private Networker networker;
	private Dispatcher dispatcher;
	private FaultToleranceSys fts;
	private DBFaultToleranceSys dbfts;

	private Thread dbftsThread;
	
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
		if (Const.ENABLE_JMS) {
			dispatcher = Dispatcher.getInstance();
			dispatcher.run();
			FaultToleranceSys.setDispatcher();
		}
		networker = Networker.getInstance(port, peerPort, ip);
		if (Const.ENABLE_JMS)
			networker.setDispatcher(dispatcher);
		networker.startRW();
		
		FaultToleranceSys.setNetwork(ip, port, peerPort);
		fts = FaultToleranceSys.getInstance();
		fts.run();
		dbfts = DBFaultToleranceSys.getInstance();
		dbftsThread = new Thread(dbfts);
		dbftsThread.setDaemon(true);
		dbftsThread.start();
	}

	public void disconnect() {
		networker.stopNetThread();
		if (Const.ENABLE_JMS)
			dispatcher.stopAll();
		fts.stop();
	}

	public boolean isConnected() {
		if(networker == null) 
			return false;
		return networker.isNetThreadRunning();
	}
}