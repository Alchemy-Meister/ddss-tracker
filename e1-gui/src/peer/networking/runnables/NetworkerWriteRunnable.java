package peer.networking.runnables;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

import bitTorrent.metainfo.InfoDictionarySingleFile;
import bitTorrent.metainfo.MetainfoFile;
import bitTorrent.tracker.protocol.udp.messages.custom.peer.AnnounceRequest;
import bitTorrent.tracker.protocol.udp.messages.custom.peer.ConnectionRequest;
import bitTorrent.tracker.protocol.udp.messages.custom.peer.ScrapeRequest;
import peer.model.Torrents;
import peer.networking.Networker;

public class NetworkerWriteRunnable implements Runnable {
	private int port;
	private String ip;
	private Networker networker = null;
	private MulticastSocket socket;
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
							System.out.println("sending connection.");
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
						//Sends announce and scraping request.
						System.out.println("sending announce and scraping.");
						
						ScrapeRequest scrapeRequest =
								new ScrapeRequest();
						scrapeRequest.setConnectionId(
								this.connectionID);
						try {
							for(MetainfoFile<InfoDictionarySingleFile> torrent :
								Torrents.getTorrents())
							{
							
								AnnounceRequest request = new AnnounceRequest();
								request.setInfoHash(
										torrent.getInfo().getHexInfoHash());
								
								scrapeRequest.addInfoHash(
										torrent.getInfo().getHexInfoHash());
								
								DatagramPacket messageOut = new DatagramPacket(
										request.getBytes(),
										request.getBytes().length,
										group, port);
								this.socket.send(messageOut);
							}
							DatagramPacket messageOut = new DatagramPacket(
									scrapeRequest.getBytes(),
									scrapeRequest.getBytes().length,
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
