package tracker.networking;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import bitTorrent.tracker.protocol.udp.messages.custom.CustomMessage;
import bitTorrent.tracker.protocol.udp.messages.custom.ka.KeepAliveM;
import tracker.exceptions.PacketParserException;

public class Bundle extends HashMap<BundleKeys, byte[]> {
	private static final long serialVersionUID = 474411219857555010L;
	
	public Bundle() {
		super();
	}
	
	public Bundle(String ip, int port, CustomMessage message) {
		super();
		this.setIP(ip);
		this.setPort(port);
		this.setMessage(message);
		this.setCurrentTimeStamp();
	}
	
	public void setIP(String ip) {
		this.put(BundleKeys.IP, ip.getBytes());
	}
	
	public void setPort(int port) {
		this.put(BundleKeys.PORT, Integer.toString(port).getBytes());
	}
	
	public void setMessage(CustomMessage message) {
		this.put(BundleKeys.MESSAGE, message.getBytes());
	}
	
	public void setCurrentTimeStamp() {
		DateTimeFormatter datefmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
		LocalDateTime now = LocalDateTime.now();
		this.put(BundleKeys.TIMESTAMP, datefmt.format(now).getBytes());
	}
	
	public String getIP() {
		return new String(this.get(BundleKeys.IP));
	}
	
	public int getPort() {
		return Integer.parseInt(new String(this.get(BundleKeys.PORT)));
	}
	
	public CustomMessage getMessage() {
		try {
			return PacketParser.parse(this.get(BundleKeys.MESSAGE));
		} catch (PacketParserException e) {
			return null;
		}
	}
	
	public String getTimestamp() {
		return new String(this.get(BundleKeys.TIMESTAMP));
	}
	
	public boolean equal(Bundle bundle) {
		return this.getIP().equals(bundle.getIP()) && 
				this.getPort() == bundle.getPort() &&
				((KeepAliveM) this.getMessage()).getId().equals(
						((KeepAliveM) bundle.getMessage()).getId()) &&
				this.getTimestamp().equals(bundle.getTimestamp());
	}
}
