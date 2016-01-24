package peer.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bitTorrent.metainfo.InfoDictionarySingleFile;
import bitTorrent.metainfo.MetainfoFile;

public class Torrents {
	private static HashMap<String, 
		MetainfoFile<InfoDictionarySingleFile>> torrents = new HashMap<>();
	
	public static void addTorrent(String hash, 
			MetainfoFile<InfoDictionarySingleFile> torrent)
	{
		torrents.put(hash, torrent);
	}
	
	public static List<MetainfoFile<InfoDictionarySingleFile>> getTorrents() {
		return new ArrayList<>(torrents.values());
	}
}
