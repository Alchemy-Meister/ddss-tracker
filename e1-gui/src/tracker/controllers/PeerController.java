package tracker.controllers;

import java.util.Random;

public class PeerController {
	
	public PeerController() {
		//TODO initialize database instance.
	}
	
	public String[][] getPeerBasicData(String id) {
		// TODO return real data from DB.
		return new String[][]{{randomIP(), randomPort()}};
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
		return new String[][]{
			{"Peer_00"},
			{"Peer_01"},
			{"Peer_02"},
			{"Peer_03"},
			{"Peer_04"},
			{"Peer_05"},
			{"Peer_06"},
			{"Peer_07"},
			{"Peer_08"},
			{"Peer_09"},
			{"Peer_10"},
			{"Peer_11"},
			{"Peer_12"},
			{"Peer_13"},
			{"Peer_14"},
			{"Peer_15"},
			{"Peer_16"}
		};
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
	
	private String randomPort() {
		Random r = new Random();
		StringBuilder sb = new StringBuilder();
		return sb.append(r.nextInt((65535 - 1024) + 1) + 1024).toString();
	}
	
	private String randomIP() {
		Random r = new Random();
		return r.nextInt(256) + "." + r.nextInt(256) +
				"." + r.nextInt(256) + "." + r.nextInt(256);
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
