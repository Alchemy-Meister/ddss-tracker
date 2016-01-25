package tracker.controllers;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

import common.utils.Utilities;
import tracker.db.DBManager;
import tracker.db.model.Peer;

public class PeerController {
	
	private DBManager db = DBManager.getInstance();
	
	public PeerController() {
		//TODO initialize database instance.
	}
	
	public String[][] getPeerBasicData(String id) {
		// TODO return real data from DB.
		int idnum = Integer.valueOf(id.split("_")[1]);
		Peer peer = null;
		this.db.connect();
		try {
			peer = this.db.getPeer(idnum);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.db.disconnect();
		if(peer != null) {
			try {
				return new String[][]{{InetAddress.getByAddress(Utilities.unpack(peer.getHost())).getHostName(), String.valueOf(peer.getPort())}};
			} catch (UnknownHostException e) {
				return new String[][]{{"", ""}};
			}
		} else {
			return new String[][]{{"", ""}};
		}
	}
	
	public String[][] getPeerTorrents(String id) {
		// TODO return real data from DB.
		return this.shuffleArray(this.getPeerTorrentsHardCodedData());
	}
	
	public String[] getPeerIDListColumnName() {
		// TODO get the column names from database.
		return new String[]{"ID"};
	}
	
	public String[] getPeerInfoColumnNames() {
		// TODO get the column names from database.
		return new String[]{"IP", "Port"};
	}
	
	public String[] getPeerTorrentColumnNames() {
		// TODO get the column names from database.
		return new String[]{"Name", "Downloading", "Uploading", "Extra"};
	}
	
	public String[][] getPeerListHardCodedData() {
		List<Peer> peerlist = null;
		this.db.connect();
		try {
			peerlist = this.db.getPeers();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.db.disconnect();
		if(peerlist != null) {
			String[][] string = new String[peerlist.size()][1];
			for(int i = 0; i < peerlist.size(); i++) {
				string[i][0] = "Peer_" + peerlist.get(i).getId();
			}
			return string;
		} else {
			return new String[][]{};
		}
	}
	
	public String[][] getPeerBasicHardCodedInfo() {
		return new String[][]{{"61.24.63.40", "65122"}};
	}
	
	public String[][] getPeerTorrentsHardCodedData() {
		return new String[][]{
			{"[ANK-Raws] Seitokai no Ichizon (BDrip 1920x1080 x264 FLAC Hi10P)",
				"35%", "2%", "u are a pirate!"},
			{"[ReinForce] Shingeki no Kyojin (BDRip 1920x1080 x264 FLAC)", 
				"13%", "5%", "u are a pirate!"},
			{"[PSXJowie] Elfen Lied | エルフェンリート (BD 1280x720) [MP4 Batch]",
				"100%", "48%", "u are a pirate!"},
			{"[Leopard-Raws] CLAYMORE (BD 1920x1080 x264 AAC(Jpn+5.1eng+EngSub))",
				"5%", "0%", "u are a pirate!"},
			{"Hatsune MIku 39's Giving Day Concert 2010 [1080p60 Hi10p AAC + 5.1 AC3][kuchikirukia]",
				"74%", "23%", "u are a pirate!"},
			{"[Poi] 40mP - 小さな自分と大きな世界 feat. 初音ミク Chiisana Jibun To Ookina Sekai feat. Hatsune Miku [MP3].zip",
				"98%", "72%", "u are a pirate!"},
			{"[ANK-Raws] Seitokai no Ichizon (BDrip 1920x1080 x264 FLAC Hi10P)",
			    "98%", "72%", "u are a pirate!"},
			{"[ANK-Raws] Seitokai no Ichizon (BDrip 1920x1080 x264 FLAC Hi10P)",
				"98%", "72%", "u are a pirate!"},
			{"[ANK-Raws] Seitokai no Ichizon (BDrip 1920x1080 x264 FLAC Hi10P)",
				"98%", "72%", "u are a pirate!"},
			{"[ANK-Raws] Seitokai no Ichizon (BDrip 1920x1080 x264 FLAC Hi10P)",
				"98%", "72%", "u are a pirate!"}
			};
	}
	
	private String[][] shuffleArray(String[][] ar)
	  {
	    Random rnd = new Random();
	    for (int i = ar.length - 1; i > 0; i--)
	    {
	      int index = rnd.nextInt(i + 1);
	      // Simple swap
	      String[] a = ar[index];
	      ar[index] = ar[i];
	      ar[i] = a;
	    }
	    return ar;
	  }
}
