package tracker.networking;

import java.util.HashMap;

import bitTorrent.tracker.protocol.udp.messages.custom.CustomMessage;
import tracker.exceptions.PacketParserException;

public class Bundle extends HashMap<BundleKeys, String> {
	private static final long serialVersionUID = 474411219857555010L;
	
	public Bundle() {
		super();
	}
	
	public Bundle(String ip, int port, CustomMessage message) {
		super();
		this.setIP(ip);
		this.setPort(port);
		this.setMessage(message);
	}
	
	public void setIP(String ip) {
		this.put(BundleKeys.IP, ip);
	}
	
	public void setPort(int port) {
		this.put(BundleKeys.PORT, Integer.toString(port));
	}
	
	public void setMessage(CustomMessage message) {
		this.put(BundleKeys.MESSAGE, message.getBytes().toString());
	}
	
	public String getIP() {
		return this.get(BundleKeys.IP);
	}
	
	public int getPort() {
		return Integer.parseInt(this.get(BundleKeys.PORT));
	}
	
	public CustomMessage getMessage() {
		try {
			return PacketParser.parse(this.get(BundleKeys.MESSAGE).getBytes());
		} catch (PacketParserException e) {
			return null;
		}
	}
}
