package tracker.networking.runnables;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

import tracker.exceptions.NetProtoException;
import tracker.networking.Networker;
import tracker.networking.Topic;

public class NetworkerReadRunnable implements Runnable {

	private int port;
	private String ip;
	private Networker networker = null;
	private MulticastSocket socket;
	private InetAddress group;

	public NetworkerReadRunnable(int port, String ip) {
	}

	public void setNetworker(Networker networker) {
		this.networker = networker;
	}

	public Networker getNetworker() {
		return this.networker;
	}

	private void notify(Topic topic, String param) {
		networker.notify(topic, param);
	}

	@Override
	public void run() {
		while (true)  {
			// Listen for 1 sec
			byte[] buffer = new byte[1024];			
			DatagramPacket messageIn = null;
			messageIn = new DatagramPacket(buffer, buffer.length);
			try {
				socket.receive(messageIn);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (messageIn != null) {
				// TODO read message type and set topic
				notify(Topic.KA, messageIn.toString()); // TODO check
			}
		}
	}

	public void init() throws NetProtoException {
		try {
			this.group = InetAddress.getByName(ip);
			MulticastSocket socket = new MulticastSocket(port);
			this.socket = socket; // TODO check
			socket.joinGroup(this.group);
		} catch (UnknownHostException e) {
			throw new NetProtoException("Unknow host: " + ip);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		try {
			this.socket.leaveGroup(group);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
