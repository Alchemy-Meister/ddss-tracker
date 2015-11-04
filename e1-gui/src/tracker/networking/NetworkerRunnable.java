package tracker.networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import tracker.exceptions.NetProtoException;

/**
 * @author Irene
 * @author Jesus
 */
public class NetworkerRunnable implements Runnable {
	
	private List<String> queue;
	private int port;
	private String ip;
	private Networker networker = null;
	private MulticastSocket socket;
	private InetAddress group;
	
	public NetworkerRunnable(int port, String ip) {
		this.queue = new LinkedList<String>(); 
	}
	
	public void setNetworker(Networker networker) {
		this.networker = networker;
	}
	
	public Networker getNetworker() {
		return this.networker;
	}

	@Override
	public void run() {
		try (MulticastSocket socket = new MulticastSocket(port)){
			this.socket = socket; // TODO check
			// Timeout of 1 second to avoid waiting forever 
			socket.setSoTimeout(1000);
			
			socket.joinGroup(group);
			
			// Send 
			if (!queue.isEmpty()) {
				String mess = queue.remove(0);
				DatagramPacket messageOut = new DatagramPacket(mess.getBytes(),
						mess.length(), group, port);
				socket.send(messageOut);
			}
			
			// Listen for 1 sec
			byte[] buffer = new byte[1024];			
			DatagramPacket messageIn = null;
			messageIn = new DatagramPacket(buffer, buffer.length);
			socket.receive(messageIn);
			if (messageIn != null)
				notify(messageIn.toString()); // TODO check

		} catch (SocketException e) {
			System.err.println("# Socket Error: " + e.getMessage());
		} catch (IOException e) {
			System.err.println("# IO Error: " + e.getMessage());
		}
	}
	
	public void put(String param) {
		queue.add(param);
	}
	
	private void notify(String param) {
		networker.receive(param);
	}

	public void init() throws NetProtoException {
		try {
			this.group = InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			throw new NetProtoException("Unknow host: " + ip);
		}
	}
	
	public void stop() {
		try {
			this.socket.leaveGroup(group);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
