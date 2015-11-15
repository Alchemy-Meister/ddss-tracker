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
	private static final String printfProto = "[ NetworkerReadRunnable] ";

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

	private void notify(Topic topic, CustomMessage message) {
		networker.notify(topic, message);
	}

	@Override
	public void run() {
		if (this.initialized) {
			while (!Thread.currentThread().isInterrupted())  {
				byte[] buffer = new byte[1024];			
				DatagramPacket messageIn = null;
				messageIn = new DatagramPacket(buffer, buffer.length);
				try {
					socket.receive(messageIn);
				} catch (IOException e) {
					Thread.currentThread().interrupt();
				}
				if (messageIn != null) {
					CustomMessage message = null;
					try {
					message = PacketParser.parse(messageIn.getData());
					} catch(PacketParserException e) {
						// TODO do something
						e.printStackTrace();
					}
					notify(Topic.KA, message);
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

	public void stop() {
		try {
			this.socket.leaveGroup(group);
			this.initialized = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
