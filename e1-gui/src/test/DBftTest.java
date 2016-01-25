package test;

import org.apache.commons.codec.digest.DigestUtils;


import bitTorrent.tracker.protocol.udp.messages.PeerInfo;
import bitTorrent.tracker.protocol.udp.messages.custom.peer.AnnounceRequest;
import bitTorrent.tracker.protocol.udp.messages.custom.peer.AnnounceRequest.Event;
import common.utils.Utilities;
import tracker.networking.Bundle;
import tracker.networking.Networker;
import tracker.networking.Topic;

public class DBftTest {

	private Networker networker;
	
	public DBftTest(Networker networker) {
		this.networker = networker;
	}

	public void sendAnnounce() {
		AnnounceRequest ar = new AnnounceRequest();
		ar.setInfoHash(DigestUtils.sha1Hex("Hello"));
		ar.setPeerId("222");
		ar.setDownloaded(1);
		ar.setLeft(2);
		ar.setUploaded(3);
		ar.setEvent(Event.STARTED);
		ar.setKey(3);
		ar.setNumWant(3);
		PeerInfo peerInfo = new PeerInfo();
		peerInfo.setIpAddress(Utilities.pack("192.168.51.10".getBytes()));
		peerInfo.setPort(2222);
		ar.setPeerInfo(peerInfo);
		System.out.println( " [ÑAPA] announce sent!");
		this.networker.notify(Topic.ANNOUNCE_R, new Bundle("192.168.51.10",
				2222, ar));
	}
}
