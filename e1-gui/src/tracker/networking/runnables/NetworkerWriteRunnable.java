package tracker.networking.runnables;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import bitTorrent.tracker.protocol.udp.messages.custom.CustomMessage;
import tracker.Const;
import tracker.exceptions.NetProtoException;
import tracker.networking.Networker;

/**
 * Runnable in charge of writing. To request a write you must put the
 * data into the queue.
 * @author Irene
 * @author Jesus
 */
public class NetworkerWriteRunnable implements Runnable {

	private List<CustomMessage> queue;
	private int port;
	private String ip;
	private Networker networker = null;
	private MulticastSocket socket;
	private InetAddress group;
	private boolean initialized = false;
	private boolean isInterrupted = false;
	private static final String printfProto = "[ NetworkerWriteRunnable] ";

	public NetworkerWriteRunnable(int port, String ip) {
		this.queue = new LinkedList<CustomMessage>(); 
		this.port = port;
		this.ip = ip;
	}

	public void setNetworker(Networker networker) {
		this.networker = networker;
	}

	public Networker getNetworker() {
		return this.networker;
	}

	/** Inserts param to que queue
	 * @param param
	 */
	public synchronized void put(CustomMessage param) {
		this.queue.add(param);
	}

	@Override
	public void run() {
		if (this.initialized) {
			while (!this.isInterrupted) {
				// Send
				CustomMessage mess = null;
				synchronized (queue) {
					if (!this.queue.isEmpty())
						mess = this.queue.remove(0);
				}
				if (mess != null) {
					byte[] messBytes = mess.getBytes();
					DatagramPacket messageOut = new DatagramPacket(
							messBytes, messBytes.length, group, port);
					try {
						this.socket.send(messageOut);
						if (Const.PRINTF)
							System.out.println(printfProto + "sent: " + mess);
					} catch (IOException e) {
						// TODO send netproto except
						e.printStackTrace();
					}
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					this.isInterrupted = true;
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
		} catch (UnknownHostException e) {
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
