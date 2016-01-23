package peer.model;

import java.util.HashMap;

import bitTorrent.metainfo.InfoDictionary;
import bitTorrent.metainfo.InfoDictionarySingleFile;
import bitTorrent.metainfo.MetainfoFile;

public class Torrents {
	private static HashMap<String, 
		MetainfoFile<InfoDictionarySingleFile>> torrents = new HashMap<>();
	
	public static void addTorrent(String hash, MetainfoFile<InfoDictionary> torrent) {
		
	}
}
