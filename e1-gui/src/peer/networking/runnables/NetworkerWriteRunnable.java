package peer.networking.runnables;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import bitTorrent.metainfo.InfoDictionarySingleFile;
import bitTorrent.metainfo.MetainfoFile;
import bitTorrent.tracker.protocol.udp.messages.custom.peer.AnnounceRequest;
import bitTorrent.tracker.protocol.udp.messages.custom.peer.ConnectionRequest;
import common.utils.Utilities;
import bitTorrent.tracker.protocol.udp.messages.custom.peer.AnnounceRequest.Event;
import peer.model.Torrents;
import peer.networking.Networker;

public class NetworkerWriteRunnable implements Runnable {
	private int port;
	private String ip;
	private Networker networker = null;
	private DatagramSocket socket;
	private InetAddress group;
	private boolean initialized = false;
	
	private static final long RETRY_TIME = 15000;
	private static final long GIVE_UP_TIME = 60000;
	private boolean cResponseReceived = false;
	private Long connectionID = null;
	
	public NetworkerWriteRunnable(int port, String ip) {
		this.port = port;
		this.ip = ip;
	}

	public void init() throws SocketException {
		try {
			this.socket = new DatagramSocket();
			this.group = InetAddress.getByName(this.ip);
			this.initialized = true;
		} catch (SocketException | UnknownHostException e) {
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
	
	public void setConnectionID(long connectionID) {
		this.connectionID = connectionID;
	}
	
	@Override
	public void run() {
		if (this.initialized) {
			boolean firstTimeBool = true;
			long firstTime = System.currentTimeMillis();
			long startTime = firstTime;
			long currentTime = startTime;
			long elapsetTime = NetworkerWriteRunnable.RETRY_TIME;
			
			while(!Thread.currentThread().isInterrupted()) {
				if(elapsetTime >= NetworkerWriteRunnable.RETRY_TIME) {
					//check if master has sent connection response.
					if(!this.cResponseReceived) {
						if(currentTime - firstTime <
								NetworkerWriteRunnable.GIVE_UP_TIME)
						{
							//Sends Connection Request.
							System.out.println("sending connect.");
							try {
								ConnectionRequest request = 
										new ConnectionRequest();
								DatagramPacket messageOut = new DatagramPacket(
										request.getBytes(),
										request.getBytes().length,
										group, port);
								this.socket.send(messageOut);
							} catch (IOException e) {
								e.printStackTrace();
							}
						} else {
							//Stop thread.
							Thread.currentThread().interrupt();
							//TODO notify panels somehow...
						}
						
					} else {
						if(firstTimeBool) {
							//Updates first time for announce requests.
							firstTime = System.currentTimeMillis();
						}
						if(currentTime - firstTime <
								NetworkerWriteRunnable.GIVE_UP_TIME)
						{
							//Sends announce and scraping request.
							System.out.println("sending announce.");
							
							try {
								for(MetainfoFile<InfoDictionarySingleFile>
								torrent : Torrents.getTorrents())
								{
									AnnounceRequest request = 
											new AnnounceRequest();
									
									request.setConnectionId(this.connectionID);
									//TODO CHECK IF SHA1
									request.setInfoHash(
											torrent.getInfo().getHexInfoHash());
									
									request.getPeerInfo().setIpAddress(
											Utilities.pack(
													InetAddress.getLocalHost()
													.getAddress()));
									
									//TODO Same port as the server, should be different.
									request.getPeerInfo().setPort(this.port);
									
									//TODO for the server get ip form int.
									// System.out.println(InetAddress.getByAddress(Utilities.unpack(asd)).getHostAddress());
									
									//Never downloads or uploads anything.
									request.setDownloaded(0);
									request.setUploaded(0);
									request.setEvent(Event.NONE);
									request.setLeft(torrent.getInfo().getLength());
									
									
									DatagramPacket messageOut =
											new DatagramPacket(
											request.getBytes(),
											request.getBytes().length,
											group, port);
									this.socket.send(messageOut);
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						} else {
							//Stop thread.
							Thread.currentThread().interrupt();
							//TODO notify panels somehow...
						}
					}
					
					startTime = System.currentTimeMillis();
				}
				currentTime = System.currentTimeMillis();
				elapsetTime = currentTime - startTime;
			}
			socket.close();
			this.connectionID = null;
			this.cResponseReceived = false;
		}
	}
}
