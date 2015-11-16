package test;

import bitTorrent.tracker.protocol.udp.messages.custom.CustomMessage;
import bitTorrent.tracker.protocol.udp.messages.custom.LongLong;
import bitTorrent.tracker.protocol.udp.messages.custom.me.MasterElectionM;
import tracker.exceptions.NetProtoException;
import tracker.exceptions.PacketParserException;
import tracker.networking.Networker;
import tracker.networking.PacketParser;
import tracker.networking.Topic;
import tracker.subsys.TrackerSubsystem;

public class RWThreadsTest {
	
	public static void main(String [] args) throws NetProtoException {
		Networker net = Networker.getInstance(9000, "228.5.6.7");
		TestingReadSubsystem systemR = new TestingReadSubsystem();
		TestingWriteSubsystem systemW = new TestingWriteSubsystem(net);
		net.subscribe(Topic.KA, systemR);
		net.subscribe(Topic.KA, systemW);
		//net.subscribe(Topic.ME, systemR);
		net.startRW();
		new Thread(systemW).start();
	}
}

class TestingReadSubsystem extends TrackerSubsystem {

	@Override
	public void receive(Topic topic, CustomMessage param) {
		try {
			System.out.println("[ TestingReadSubsystem ] received-> topic: " +
					topic + ", param: "
					+ PacketParser.parse(param.getBytes()).toString());
		} catch (PacketParserException e) {
			e.printStackTrace();
		}
	}
	
}

class TestingWriteSubsystem extends TrackerSubsystem implements Runnable {

	private Networker net;
	
	public TestingWriteSubsystem(Networker net) {
		this.net = net;
	}
	
	@Override
	public void receive(Topic topic, CustomMessage param) {
		try {
			System.out.println("[ TestingWriteSubsystem ] received-> topic: " +
					topic + ", param: "
					+ PacketParser.parse(param.getBytes()).toString());
		} catch (PacketParserException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// Should be received by all
		//net.publish(Topic.KA,  new KeepAliveM(new BigInteger("1")));
		// Should not be received
		//net.publish(Topic.DS_COMMIT, new KeepAliveM(new BigInteger("0")));
		// By all
		//net.publish(Topic.KA, new KeepAliveM(new BigInteger("23")));
		//
		net.publish(Topic.ME, new MasterElectionM(new LongLong("9996")));
	}
	
	
}