package tracker.networking.runnables;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import bitTorrent.tracker.protocol.udp.messages.BitTorrentUDPMessage;

public class NetworkerPeerWriteRunnable implements Runnable {
	
	private BitTorrentUDPMessage message;
	private int port;
	private String ip;
	
	public NetworkerPeerWriteRunnable(
			BitTorrentUDPMessage message, String ip, int port) 
	{
		this.message = message;
		this.port = port;
		this.ip = ip;
	}

	@Override
	public void run() {
		try {
			@SuppressWarnings("resource")
			DatagramSocket socket = new DatagramSocket();
				DatagramPacket datagram = new DatagramPacket(
						message.getBytes(), message.getBytes().length,
						InetAddress.getByName(ip), port);
				socket.send(datagram);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
