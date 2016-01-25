package peer.model;

import java.util.concurrent.ConcurrentHashMap;

public class Torrent {
	public static ConcurrentHashMap<String, Torrent> torrents = 
		new ConcurrentHashMap<>();
	
	private int leechers;
	private int seeders;
	private long interval;
	private long size;
	
	public Torrent(long size) {
		this.leechers = 0;
		this.seeders = 0;
		this.interval = 0;
		this.size = size;
	}
	
	public Torrent(int leechers, int seeders, long interval, long size) {
		this.leechers = leechers;
		this.seeders = seeders;
		this.interval = interval;
		this.size = size;
	}
	
	public int getLeechers() {
		return leechers;
	}
	
	public void setLeechers(int leechers) {
		this.leechers = leechers;
	}

	public int getSeeders() {
		return seeders;
	}

	public void setSeeders(int seeders) {
		this.seeders = seeders;
	}

	public long getInterval() {
		return interval;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}
	
	public long getSize() {
		return this.size;
	}
	
	public void setSize(long size) {
		this.size = size;
	}
}
