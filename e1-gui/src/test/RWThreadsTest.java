package test;

import java.math.BigInteger;

import bitTorrent.tracker.protocol.udp.messages.custom.CustomMessage;
import bitTorrent.tracker.protocol.udp.messages.custom.ka.KeepAliveM;
import tracker.exceptions.NetProtoException;
import tracker.networking.Networker;
import tracker.networking.Topic;
import tracker.subsys.TrackerSubsystem;

public class RWThreadsTest {
	
	public static void main(String [] args) throws NetProtoException {
		Networker net = Networker.getInstance(9000, "228.5.6.7");
		TestingReadSubsystem systemR = new TestingReadSubsystem();
		TestingWriteSubsystem systemW = new TestingWriteSubsystem(net);
		net.subscribe(Topic.KA, systemR);
		net.subscribe(Topic.KA, systemW);
		net.startRW();
		new Thread(systemW).start();
	}
}

class TestingReadSubsystem extends TrackerSubsystem {

	@Override
	public void receive(Topic topic, CustomMessage param) {
		System.out.println("[ TestingReadSubsystem ] received-> topic: " +
				topic);
		System.out.println("[ TestingReadSubsystem ] received-> param: " +
				param);
	}
	
}

class TestingWriteSubsystem extends TrackerSubsystem implements Runnable {

	private Networker net;
	
	public TestingWriteSubsystem(Networker net) {
		this.net = net;
	}
	
	@Override
	public void receive(Topic topic, CustomMessage param) {
		System.out.println("[ TestingWriteSubsystem ] received-> topic: " +
				topic);
		System.out.println("[ TestingWriteSubsystem ] received-> param: " +
				param + " topic: " + topic);
		
	}

	@Override
	public void run() {
		// Should be received by all
		net.publish(Topic.KA,  new KeepAliveM(new BigInteger("1")));
		// Should not be received
		net.publish(Topic.DS_COMMIT, new KeepAliveM(new BigInteger("0")));
		// By all
		net.publish(Topic.KA, new KeepAliveM(new BigInteger("2")));
	}
	
	
}