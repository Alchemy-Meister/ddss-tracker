package peer.networking.runnables;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;

import bitTorrent.tracker.protocol.udp.messages.BitTorrentUDPMessage.Action;
import bitTorrent.tracker.protocol.udp.messages.BitTorrentUDPRequestMessage;
import bitTorrent.tracker.protocol.udp.messages.custom.peer.AnnounceRequest;
import bitTorrent.tracker.protocol.udp.messages.custom.peer.AnnounceResponse;
import bitTorrent.tracker.protocol.udp.messages.custom.peer.ConnectionResponse;
import bitTorrent.tracker.protocol.udp.messages.custom.peer.ResponseError;
import peer.model.Torrent;
import peer.model.Transaction;
import peer.networking.Networker;

public class NetworkerReadRunnable implements Runnable {
	private Networker networker = null;
	private DatagramSocket socket;
	
	public NetworkerReadRunnable(DatagramSocket socket) {
		this.socket = socket;
	}

	public void setNetworker(Networker networker) {
		this.networker = networker;
	}
	
	public void interrupt() {
		this.socket.close();
	}
	
	@Override
	public void run() {
		while(!Thread.currentThread().isInterrupted()) {
			try {
				byte[] buffer = new byte[20000];	
				DatagramPacket messageIn = new DatagramPacket(
						buffer, buffer.length);
				
				this.socket.receive(messageIn);
				
				if(messageIn != null) {
					
					byte[] response = messageIn.getData();
					ByteBuffer byteBuffer = ByteBuffer.wrap(response);
					
					Action action = Action.valueOf(byteBuffer.getInt(0));
					
					if(action != null) {
						switch (action) {
						case CONNECT:
							ConnectionResponse cResponse = 
							ConnectionResponse.parse(response);
							networker.setConnectionId(
									cResponse.getConnectionId());
							networker.receivedConnectionMessage();
							System.out.println("connect received.");
							break;
						case ANNOUNCE:
							AnnounceResponse aResponse = AnnounceResponse.parse(
									response);
							BitTorrentUDPRequestMessage message =
									Transaction.transactions.remove(
									aResponse.getTransactionId());
							
							if(message != null &&
									message instanceof AnnounceRequest) {
								Torrent torrent = 
										Torrent.torrents.get(
												((AnnounceRequest) message)
												.getInfoHash());
								torrent.setLeechers(aResponse.getLeechers());
								torrent.setSeeders(aResponse.getSeeders());
								
								networker.notify(((AnnounceRequest) message)
										.getInfoHash());
							}
							System.out.println("announce received.");
							break;
						case SCRAPE:
							//TODO Nothing to do here.
							break;
						case ERROR:
							ResponseError eResponse = ResponseError.parse(
									response);
							//Prints string to error output.
							System.err.println(eResponse.getErrorString());
							break;
						}
					}
				}
			} catch (IOException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
}
