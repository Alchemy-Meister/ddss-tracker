package test;

import java.util.ArrayList;
import java.util.List;

import bitTorrent.tracker.protocol.udp.messages.custom.LongLong;
import bitTorrent.tracker.protocol.udp.messages.custom.ds.DSCommitM;
import bitTorrent.tracker.protocol.udp.messages.custom.ds.DSDoneM;
import bitTorrent.tracker.protocol.udp.messages.custom.ds.DSReadyM;
import bitTorrent.tracker.protocol.udp.messages.custom.hi.Contents;
import bitTorrent.tracker.protocol.udp.messages.custom.hi.HelloCloseM;
import bitTorrent.tracker.protocol.udp.messages.custom.hi.HelloM;
import bitTorrent.tracker.protocol.udp.messages.custom.hi.HelloResponseM;
import bitTorrent.tracker.protocol.udp.messages.custom.ka.KeepAliveM;
import bitTorrent.tracker.protocol.udp.messages.custom.me.MasterElectionM;
import tracker.exceptions.NetProtoException;
import tracker.exceptions.PacketParserException;
import tracker.networking.Bundle;
import tracker.networking.Networker;
import tracker.networking.PacketParser;
import tracker.networking.Topic;
import tracker.subsys.TrackerSubsystem;

public class RWThreadsTest {

	public static void main(String [] args) throws NetProtoException {
		Networker net = Networker.getInstance(9000, "228.5.6.7");
		TestingReadSubsystem systemR = new TestingReadSubsystem();
		TestingWriteSubsystem systemW = new TestingWriteSubsystem(net);
		//net.subscribe(Topic.KA, systemR);
		//net.subscribe(Topic.HI, systemR);
		//net.subscribe(Topic.KA, systemW);
		//net.subscribe(Topic.ME, systemR);
		net.subscribe(Topic.DS_READY, systemR);
		net.subscribe(Topic.DS_COMMIT, systemR);
		net.subscribe(Topic.DS_DONE, systemR);
		net.startRW();
		new Thread(systemW).start();
	}
}

class TestingReadSubsystem extends TrackerSubsystem {


	@Override
	public void receive(Topic topic, Bundle bundle) {
		try{
			System.err.println("[ TestingReadSubsystem ] received-> topic: " +
					topic + ", param: "
					+ PacketParser.parse(bundle.getMessage().getBytes()));
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
	public void run() {;
	/*
		net.publish(Topic.KA, new KeepAliveM(new LongLong("23"))); // ok
		net.publish(Topic.ME, new MasterElectionM(new LongLong("9996"))); // ok
		net.publish(Topic.HI, new HelloM(23));// ok
		net.publish(Topic.HI, new HelloCloseM(21, new LongLong("9393"),
				new LongLong("9494"))); // ok 
	 */
	/*// hi response -> ok
		List<Contents> triplets = new ArrayList<Contents>();
		short s = 8;
		triplets.add(new Contents(new LongLong("1"), 2, s));
		net.publish(Topic.HI, new HelloResponseM(10, new LongLong("11"),
				new LongLong("2"), triplets)); // ok
	 */
		List<LongLong> info_hashes = new ArrayList<LongLong>();
		info_hashes.add(new LongLong("11"));
		info_hashes.add(new LongLong("12"));
		info_hashes.add(new LongLong("13"));
		info_hashes.add(new LongLong("14"));
		info_hashes.add(new LongLong("15"));
		info_hashes.add(new LongLong("16"));
		net.publish(Topic.DS_READY, new DSReadyM(2, 5, 10, info_hashes)); // ok
		net.publish(Topic.DS_COMMIT, new DSCommitM(10, 10, 10)); // ok
		net.publish(Topic.DS_DONE, new DSDoneM(222222222));
	}

	@Override
	public void receive(Topic topic, Bundle bundle) {
		try {
			System.err.println("[ TestingWriteSubsystem ] received-> topic: " +
					topic + ", param: "
					+ PacketParser.parse(bundle.getMessage().getBytes()).toString());
		} catch (PacketParserException e) {
			e.printStackTrace();
		}

	}


}