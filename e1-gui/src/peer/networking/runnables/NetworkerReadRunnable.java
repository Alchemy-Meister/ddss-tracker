package peer.networking.runnables;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;

import bitTorrent.tracker.protocol.udp.messages.BitTorrentUDPMessage.Action;
import bitTorrent.tracker.protocol.udp.messages.custom.peer.AnnounceResponse;
import bitTorrent.tracker.protocol.udp.messages.custom.peer.ConnectionResponse;
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
				byte[] buffer = new byte[5000];	
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
							networker.setReceivedConnectionMessage(true);
							System.out.println(cResponse);
							break;
						case ANNOUNCE:
							AnnounceResponse aResponse = AnnounceResponse.parse(
									response);
					
							//TODO publish response
							System.out.println(aResponse);
							break;
						case SCRAPE:
							
							break;
						case ERROR:
							
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
