package tracker.networking.runnables;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import bitTorrent.tracker.protocol.udp.messages.BitTorrentUDPMessage.Action;
import bitTorrent.tracker.protocol.udp.messages.custom.peer.AnnounceRequest;
import bitTorrent.tracker.protocol.udp.messages.custom.peer.AnnounceResponse;
import bitTorrent.tracker.protocol.udp.messages.custom.peer.ConnectionRequest;
import bitTorrent.tracker.protocol.udp.messages.custom.peer.ConnectionResponse;
import tracker.exceptions.NetProtoException;
import tracker.networking.Bundle;
import tracker.networking.Networker;
import tracker.networking.Topic;
import tracker.subsys.cfts.IpIdTable;

public class NetworkerPeerReadRunnable implements Runnable {

	private int port;
	private String ip;
	private Networker networker = null;
	private MulticastSocket socket;
	private InetAddress group;
	private boolean initialized = false;
	
	
	public NetworkerPeerReadRunnable(int port, String ip) {
		this.port = port;
		this.ip = ip;
	}
	
	public void init() throws NetProtoException {
		try {
			this.socket = new MulticastSocket(port);
			this.group = InetAddress.getByName(ip);
			this.socket.joinGroup(this.group);
			this.initialized = true;
		} catch (UnknownHostException | SocketException e) {
			throw new NetProtoException("Unknow host: " + ip);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setNetworker(Networker networker) {
		this.networker = networker;
	}
	
	private void notify(Topic topic, Bundle bundle) {
		this.networker.notify(topic, bundle);
	}
	
	public void setIP(String ip) {
		this.ip = ip;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	@Override
	public void run() {
		if(this.initialized) {
			while(!Thread.currentThread().isInterrupted()) {
				try {
					byte[] buffer = new byte[5000];
					DatagramPacket messageIn = 
							new DatagramPacket(buffer, buffer.length);
					this.socket.receive(messageIn);
					
					ByteBuffer byteBuffer = ByteBuffer.wrap(
							messageIn.getData());
					Action action = Action.valueOf(byteBuffer.getInt(8));
					
					if(action != null) {
						switch (action) {
						case CONNECT:
							ConnectionRequest cRequest = ConnectionRequest.parse(
									messageIn.getData());
							ConnectionResponse response =
									new ConnectionResponse(
											cRequest.getConnectionId(),
											cRequest.getTransactionId());
							if(IpIdTable.getInstance().amIMaster()) {
								new Thread(new NetworkerPeerWriteRunnable(
										response,
										messageIn.getAddress().getHostAddress(),
										messageIn.getPort()))
								.start();	
							}	
							break;
						case ANNOUNCE:
							AnnounceRequest aRequest = AnnounceRequest.parse(
									messageIn.getData());
							notify(Topic.ANNOUNCE_R,
									// line too long, U_U
									new Bundle(messageIn.getAddress().getHostAddress(),
											messageIn.getPort(),
											aRequest));
							AnnounceResponse aResponse = new AnnounceResponse();
							//TODO SET SHITTY PARAMS.
							if(IpIdTable.getInstance().amIMaster()) {
								new Thread(new NetworkerPeerWriteRunnable(
										aResponse,
										messageIn.getAddress().getHostAddress(),
										messageIn.getPort()))
								.start();	
							}	
							break;
						case SCRAPE:
							//TODO DO NOTHING, COZ FUCK U.
							break;
						case ERROR:
							//TODO WTF??
							break;
						}
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}
	}

}
