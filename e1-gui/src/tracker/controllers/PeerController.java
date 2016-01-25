package tracker.controllers;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.List;

import bitTorrent.tracker.protocol.udp.messages.custom.hi.Contents;
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
				return new String[][]{{InetAddress.getByAddress(
						Utilities.unpack(peer.getHost())).getHostAddress(),
					Short.valueOf(peer.getPort()).toString()}};
			} catch (UnknownHostException e) {
				return new String[][]{{"", ""}};
			}
		} else {
			return new String[][]{{"", ""}};
		}
	}
	
	public String[][] getPeerTorrents(String id) {
		int idnum = Integer.valueOf(id.split("_")[1]);
		
		List<Contents> contentList = null;
		this.db.connect();
		try {
			contentList = this.db.getAllContentsPeer(idnum);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.db.disconnect();
		if(contentList != null) {
			String[][] stringContent = 
				new String[contentList.size()][getPeerTorrentColumnNames()
				                               .length];
				for(int i = 0; i < contentList.size(); i++) {
					stringContent[i][0] = contentList.get(i).getInfo_hash().toString();
					stringContent[i][1] = Integer.valueOf(0).toString();
					stringContent[i][2] = Integer.valueOf(0).toString();
					stringContent[i][3] = "u are a pirate!";
				}
				return stringContent;
		} else {
			return new String[][]{{}};
		}
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
		return new String[]{"Hash", "Downloading", "Uploading", "Extra"};
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
		return new String[][]{{"", ""}};
	}
}
