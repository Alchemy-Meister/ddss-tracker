package test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.commons.codec.digest.DigestUtils;


import bitTorrent.tracker.protocol.udp.messages.PeerInfo;
import bitTorrent.tracker.protocol.udp.messages.custom.peer.AnnounceRequest;
import bitTorrent.tracker.protocol.udp.messages.custom.peer.AnnounceRequest.Event;
import common.utils.Utilities;

public class DBftTest {
	
	public static void main(String[] args) throws SocketException, UnknownHostException {
		AnnounceRequest ar = new AnnounceRequest();
		System.out.println(DigestUtils.sha1Hex("Hello world"));
		ar.setInfoHash(DigestUtils.sha1Hex("Hello world"));
		ar.setPeerId("222");
		ar.setDownloaded(1);
		ar.setLeft(2);
		ar.setUploaded(3);
		ar.setEvent(Event.STARTED);
		ar.setKey(3);
		ar.setNumWant(3);
		PeerInfo peerInfo = new PeerInfo();
		peerInfo.setIpAddress(Utilities.pack(
				InetAddress.getByName("192.168.51.10").getAddress()));
		peerInfo.setPort(2222);
		ar.setPeerInfo(peerInfo);
		DatagramSocket socket = new DatagramSocket();
		try {
			DatagramPacket messageOut = new DatagramPacket(
					ar.getBytes(),
					ar.getBytes().length,
					InetAddress.getByName("228.1.1.4"), 1235);
			socket.send(messageOut);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println( "\n [ÑAPA-TEST] Announce sent!\n");
		socket.close();
	}
}
