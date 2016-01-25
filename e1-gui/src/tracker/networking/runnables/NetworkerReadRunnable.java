package tracker.networking.runnables;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

import bitTorrent.tracker.protocol.udp.messages.custom.CustomMessage;
import tracker.Const;
import tracker.exceptions.NetProtoException;
import tracker.exceptions.PacketParserException;
import tracker.networking.Bundle;
import tracker.networking.Networker;
import tracker.networking.PacketParser;
import tracker.networking.Topic;

/**
 * Runnable in charge of continiously reading. This runnable notifies to 
 * every subscriber of a certain topic that a message with such topic has
 * arrived. (You must subscribe to the topic first)
 * @author Irene
 * @author Jesus
 */
public class NetworkerReadRunnable implements Runnable {

	private int port;
	private String ip;
	private Networker networker = null;
	private MulticastSocket socket;
	private InetAddress group;
	private boolean initialized = false;
	private static final String printfProto = "[ NRR ] ";

	public NetworkerReadRunnable(int port, String ip) {
		this.port = port;
		this.ip = ip;
	}

	public void setNetworker(Networker networker) {
		this.networker = networker;
	}

	public Networker getNetworker() {
		return this.networker;
	}

	private void notify(Topic topic, Bundle bundle) {
		networker.notify(topic, bundle);
	}

	@Override
	public void run() {
		if (this.initialized) {
			while (!Thread.currentThread().isInterrupted())  {
				byte[] buffer = new byte[256];			
				DatagramPacket messageIn = null;
				messageIn = new DatagramPacket(buffer, buffer.length);
				try {
					socket.receive(messageIn);
					if (messageIn != null) {
						if (Const.PRINTF) {
							String prin = (printfProto + "in hex: ");
							for (byte i : buffer)
								prin += String.format("0x%02X ", i);
							System.out.println(prin +"\n");
						}
						CustomMessage message = null;
						try {
							message = PacketParser.parse(messageIn.getData());
							this.notify(Topic.topicFromType(message.getType()),
									new Bundle(
											messageIn.getAddress().getHostAddress(),
											messageIn.getPort(), message));
						} catch(PacketParserException e) {
							// TODO do something
							e.printStackTrace();
						}			
					}
				} catch (IOException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	public void init() throws NetProtoException {
		try {
			this.group = InetAddress.getByName(ip);
			MulticastSocket socket = new MulticastSocket(port);
			this.socket = socket; // TODO check
			socket.joinGroup(this.group);
			this.initialized = true;
		} catch (UnknownHostException | SocketException e) {
			throw new NetProtoException("Unknow host: " + ip);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void interrupt() {
		this.socket.close();
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public void setIP(String ip) {
		this.ip = ip;
	}
	
	public void stop() {
		try {
			this.socket.leaveGroup(group);
			this.initialized = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
