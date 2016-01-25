package peer.networking.runnables;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import bitTorrent.tracker.protocol.udp.messages.BitTorrentUDPRequestMessage;
import bitTorrent.tracker.protocol.udp.messages.custom.peer.AnnounceRequest;
import bitTorrent.tracker.protocol.udp.messages.custom.peer.ConnectionRequest;
import common.utils.Utilities;
import bitTorrent.tracker.protocol.udp.messages.custom.peer.AnnounceRequest.Event;
import peer.model.Torrent;
import peer.model.Transaction;

public class NetworkerWriteRunnable implements Runnable {
	private int port;
	private String ip;
	private DatagramSocket socket;
	private InetAddress group;
	private boolean initialized = false;
	
	private static final long RETRY_TIME = 15000;
	private static final long EXPIRED_TIME = 60000;
	private static final long GIVE_UP_TIME = 60000;
	private boolean cResponseReceived = false;
	private Long connectionID = null;
	
	private boolean firstTimeBool = true;
	private long connectionTime = 0;
	private long firstTime = 0;
	
	private volatile long startTime;
	private volatile long currentTime;
	private volatile long elapsetTime = NetworkerWriteRunnable.RETRY_TIME;
	
	public NetworkerWriteRunnable(int port, String ip, DatagramSocket socket) {
		this.port = port;
		this.ip = ip;
		this.socket = socket;
	}

	public void init() throws SocketException {
		try {
			this.group = InetAddress.getByName(this.ip);
			this.initialized = true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public void setIP(String ip) {
		this.ip = ip;
	}
	
	public void setPort(int port) {
		this.port = port;
	}

	public void cResponseReceived() {
		this.firstTimeBool = true;
		this.cResponseReceived = true;
		this.elapsetTime = NetworkerWriteRunnable.RETRY_TIME;
	}
	
	public void setConnectionID(long connectionID) {
		this.connectionID = connectionID;
	}
	
	private void sendMessage(final BitTorrentUDPRequestMessage message,
			final DatagramSocket socket)
	{ 
		Thread write = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					DatagramPacket messageOut = new DatagramPacket(
							message.getBytes(),
							message.getBytes().length,
							group, port);
					socket.send(messageOut);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		write.start();
	}
	
	private AnnounceRequest createAnnounce(String hash, long size) {
		AnnounceRequest request = 
				new AnnounceRequest();
		
		request.setConnectionId(this.connectionID);
		//TODO CHECK IF SHA1
		request.setInfoHash(hash);
		try {
			request.getPeerInfo().setIpAddress(
			Utilities.pack(
					InetAddress.getLocalHost()
					.getAddress()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		//TODO Same port as the server, should be different.
		request.getPeerInfo().setPort(this.port);
		
		//Never downloads or uploads anything.
		request.setDownloaded(0);
		request.setUploaded(0);
		request.setEvent(Event.NONE);
		request.setLeft(size);
		
		return request;
	}
	
	@Override
	public void run() {
		if (this.initialized) {
			while(!Thread.currentThread().isInterrupted()) {
				if(Torrent.torrents.size() > 0) {
					if(elapsetTime >= NetworkerWriteRunnable.RETRY_TIME) {
						//check if master has sent connection response.
						if(!this.cResponseReceived) {
							if(currentTime - firstTime <
									NetworkerWriteRunnable.GIVE_UP_TIME)
							{
								//Sends Connection Request.
								System.out.println("sending connect.");
								
								ConnectionRequest request =
										new ConnectionRequest();
								
								Transaction.transactions.put(
										request.getTransactionId(), request);
								
								this.sendMessage(request,
										this.socket);
							} else {
								//Stop thread.
								Thread.currentThread().interrupt();
								System.err.println("Give up sending connection requests.");
							}
							
						} else {
							if(firstTimeBool) {
								//Updates first time for announce requests.
								firstTime = System.currentTimeMillis();
								connectionTime = System.currentTimeMillis();
								firstTimeBool = false;
							}
							
							if(currentTime - connectionTime >=
									NetworkerWriteRunnable.EXPIRED_TIME)
							{
								System.out.println("connection session ended.");
								firstTimeBool = true;
								this.cResponseReceived = false;
								firstTime = System.currentTimeMillis();
							} else if(currentTime - firstTime <
									NetworkerWriteRunnable.GIVE_UP_TIME)
							{
								//Sends announce and scraping request.
								System.out.println("sending announce.");
								
								for(String hash : Torrent.torrents.keySet())
								{
									Torrent torrent = 
											Torrent.torrents.get(hash);
									AnnounceRequest request =
											this.createAnnounce(
													hash,torrent.getSize());
									
									Transaction.transactions.put(
										request.getTransactionId(), request);
									
									this.sendMessage(request, this.socket);
								}
								
							} else {
								//Stop thread.
								Thread.currentThread().interrupt();
								System.err.println("Give up sending announce requests.");
							}
						}
						startTime = System.currentTimeMillis();
					}
					currentTime = System.currentTimeMillis();
					elapsetTime = currentTime - startTime;
				}
			}
			socket.close();
			this.connectionID = null;
			this.cResponseReceived = false;
		}
	}
}
