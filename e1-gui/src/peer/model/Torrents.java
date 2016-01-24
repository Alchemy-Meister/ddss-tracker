package peer.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import bitTorrent.metainfo.InfoDictionarySingleFile;
import bitTorrent.metainfo.MetainfoFile;

public class Torrents {
	private static ConcurrentHashMap<String, 
		MetainfoFile<InfoDictionarySingleFile>> torrents = 
		new ConcurrentHashMap<>();
	
	public static void addTorrent(String hash, 
			MetainfoFile<InfoDictionarySingleFile> torrent)
	{
		torrents.put(hash, torrent);
	}
	
	public static List<MetainfoFile<InfoDictionarySingleFile>> getTorrents() {
		return new ArrayList<>(torrents.values());
	}
}
