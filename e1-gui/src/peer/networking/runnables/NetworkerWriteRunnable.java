package peer.networking.runnables;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

import bitTorrent.tracker.protocol.udp.messages.custom.peer.AnnounceRequest;
import bitTorrent.tracker.protocol.udp.messages.custom.peer.ConnectionRequest;
import peer.networking.Networker;

public class NetworkerWriteRunnable implements Runnable {
	private int port;
	private String ip;
	private Networker networker = null;
	private MulticastSocket socket;
	private InetAddress group;
	private boolean initialized = false;
	
	private static final long RETRYTIME = 15000;
	private boolean cResponseReceived = false;
	
	public NetworkerWriteRunnable(int port, String ip) {
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
		} catch (UnknownHostException | SocketException  e) {
			throw new SocketException();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setIP(String ip) {
		this.ip = ip;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public void setNetworker(Networker networker) {
		this.networker = networker;
	}

	public void setcResponseReceived(boolean cResponseReceived) {
		this.cResponseReceived = cResponseReceived;
	}
	
	@Override
	public void run() {
		if (this.initialized) {
			long startTime = System.currentTimeMillis();
			long currentTime = startTime;
			long elapsetTime = NetworkerWriteRunnable.RETRYTIME;
			
			while(!Thread.currentThread().isInterrupted()) {
				if(elapsetTime >= NetworkerWriteRunnable.RETRYTIME) {
					//check if master has sent connection response.
					if(!this.cResponseReceived) {
						//Sends Connection Request.
						System.out.println("sending connection.");
						try {
							ConnectionRequest request = new ConnectionRequest();
							DatagramPacket messageOut = new DatagramPacket(
									request.getBytes(),
									request.getBytes().length,
									group, port);
							this.socket.send(messageOut);
							
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else {
						//Sends announce request.
						System.out.println("sending announce.");
						try {
							AnnounceRequest request = new AnnounceRequest();
							DatagramPacket messageOut = new DatagramPacket(
									request.getBytes(),
									request.getBytes().length,
									group, port);
								this.socket.send(messageOut);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					startTime = System.currentTimeMillis();
				}
				currentTime = System.currentTimeMillis();
				elapsetTime = currentTime - startTime;
			}
		}
	}
}
