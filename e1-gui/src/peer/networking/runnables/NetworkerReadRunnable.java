package peer.networking.runnables;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

import bitTorrent.tracker.protocol.udp.messages.BitTorrentUDPMessage.Action;
import bitTorrent.tracker.protocol.udp.messages.custom.peer.ConnectionRequest;
import peer.networking.Networker;

public class NetworkerReadRunnable implements Runnable {
	private int port;
	private String ip;
	private Networker networker = null;
	private MulticastSocket socket;
	private InetAddress group;
	private boolean initialized = false;
	
	public NetworkerReadRunnable(int port, String ip) {
		this.port = port;
		this.ip = ip;
	}

	public void init() throws SocketException {
		try {
			this.group = InetAddress.getByName(ip);
			MulticastSocket socket = new MulticastSocket(port);
			this.socket = socket; // TODO check
			socket.joinGroup(this.group);
			this.initialized = true;
		} catch (UnknownHostException | SocketException e) {
			throw new SocketException();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setNetworker(Networker networker) {
		this.networker = networker;
	}
	
	public void setIP(String ip) {
		this.ip = ip;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	private boolean isConnectionMessage(DatagramPacket packet) {
		boolean isConnectionMessage = true;
		if(packet.getLength() == 16) {
			ConnectionRequest request = ConnectionRequest.parse(
					packet.getData());
			if(!request.getAction().equals(Action.CONNECT)) {
				isConnectionMessage = false;
			}
		} else {
			isConnectionMessage = false;
		}
		
		return isConnectionMessage;
	}
	
	@Override
	public void run() {
		if(this.initialized) {
			while(!Thread.currentThread().isInterrupted()) {
				try {
					byte[] buffer = new byte[1000];	
					DatagramPacket messageIn = new DatagramPacket(
							buffer, buffer.length,
							group, port);
					this.socket.receive(messageIn);
					if(messageIn != null) {
						if(isConnectionMessage(messageIn)) {
							networker.setReceivedConnectionMessage(true);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
