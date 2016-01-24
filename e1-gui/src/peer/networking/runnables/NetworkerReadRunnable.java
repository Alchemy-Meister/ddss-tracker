package peer.networking.runnables;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import bitTorrent.tracker.protocol.udp.messages.BitTorrentUDPMessage.Action;
import bitTorrent.tracker.protocol.udp.messages.custom.peer.AnnounceResponse;
import bitTorrent.tracker.protocol.udp.messages.custom.peer.ConnectionResponse;
import peer.networking.Networker;

public class NetworkerReadRunnable implements Runnable {
	private int port;
	private String ip;
	private Networker networker = null;
	private DatagramSocket socket;
	private InetAddress group;
	private boolean initialized = false;
	
	public NetworkerReadRunnable(int port, String ip) {
		this.port = port;
		this.ip = ip;
	}

	public void init() throws SocketException {
		try {
			this.socket = new DatagramSocket();
			this.group = InetAddress.getByName(this.ip);
			this.initialized = true;
		} catch (SocketException | UnknownHostException e) {
			throw new SocketException();
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
	
	public void interrupt() {
		this.socket.close();
	}
	
	private boolean isConnectionMessage(DatagramPacket packet) {
		boolean isConnectionMessage = true;
		if(packet.getLength() == 16) {
			ConnectionResponse response = ConnectionResponse.parse(
					packet.getData());
			if(!response.getAction().equals(Action.CONNECT)) {
				isConnectionMessage = false;
			}
		} else {
			isConnectionMessage = false;
		}
		
		return isConnectionMessage;
	}
	
	private boolean isAnnounceMessage(DatagramPacket packet) {
		boolean isAnnounceMessage = true;
		if(packet.getLength() >= 160) {
			AnnounceResponse response = AnnounceResponse.parse(
					packet.getData());
			if(!response.getAction().equals(Action.ANNOUNCE)) {
				isAnnounceMessage = false;
			}
		} else {
			isAnnounceMessage = false;
		}
		
		return isAnnounceMessage;
	}
	
	@Override
	public void run() {
		if(!initialized) {
			while(!Thread.currentThread().isInterrupted()) {
				try {
					byte[] buffer = new byte[1000];	
					DatagramPacket messageIn = new DatagramPacket(
							buffer, buffer.length,
							group, port);
					this.socket.receive(messageIn);
					if(messageIn != null) {
						System.out.println("received something");
						if(isConnectionMessage(messageIn)) {
							if(this.socket.getRemoteSocketAddress() != null && 
									this.socket.getRemoteSocketAddress()
									!= this.socket.getLocalSocketAddress())
							{
								ConnectionResponse response = 
										ConnectionResponse.parse(
												messageIn.getData());
								networker.setConnectionId(
										response.getConnectionId());
								networker.setReceivedConnectionMessage(true);
							}
						} else if(isAnnounceMessage(messageIn)) {
							AnnounceResponse response = AnnounceResponse.parse(
											messageIn.getData());
							
							//TODO publish response
							System.out.println(response);
						}
					}
				} catch (IOException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}
}
