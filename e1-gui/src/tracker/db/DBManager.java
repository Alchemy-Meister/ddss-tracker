package tracker.db;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import bitTorrent.tracker.protocol.udp.messages.custom.SHA1;
import bitTorrent.tracker.protocol.udp.messages.custom.hi.Contents;
import common.utils.Utilities;
import tracker.Const;
import tracker.db.model.Peer;

/** Manages the sqlite database
 * @author Irene
 * @author Jesus
 */
public class DBManager {

	private static String dbname = null;
	private static DBManager instance = null;
	private Connection conn = null;

	private DBManager() {
		Random r = new Random(System.currentTimeMillis());
		dbname = "";
		for (int i = 0; i<15; i++)
			dbname += ((char) (r.nextInt(26) + 'a'));
		if (Const.PRINTF_DB)
			System.out.println(" [DB] Assigned DB name: " + dbname);
	}

	public static synchronized DBManager getInstance() {
		if (instance == null)
			instance = new DBManager();
		return instance;
	}

	public void connect() {
		if (conn == null) {
			try {
				Class.forName("org.sqlite.JDBC");
				conn = DriverManager.getConnection(
						"jdbc:sqlite:" + dbname + ".sqlite");
				Statement sta = conn.createStatement();
				sta.execute("PRAGMA foreign_keys = ON");
				sta.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void disconnect() {
		if (conn != null) {
			try {
				conn.close();
				conn = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void createTables() throws SQLException {
		connect();
		Statement sta = conn.createStatement();
		String peer = "CREATE TABLE PEERINFO (" +
				" ID INTEGER NOT NULL PRIMARY KEY," +
				" HOST VARCHAR(255), PORT INTEGER);";
		String que = "CREATE TABLE CONTENTS (" +
				" SHA1 CHAR(40) NOT NULL," +
				" PEER_ID INTEGER NOT NULL REFERENCES PEERINFO (ID) ON DELETE CASCADE,"
				+ "PRIMARY KEY (SHA1, PEER_ID)"
				+ ");"; 
		sta.executeUpdate(peer);
		sta.executeUpdate(que);
		sta.close();
		disconnect();
	}

	private int getMaxId() throws SQLException {
		// prinary key autoincrement seems not to work the way I want it to
		// behave, since db is not critical I am doing this
		String query = "SELECT max(ID) from PEERINFO;";
		Statement pre = conn.createStatement();
		ResultSet re = pre.executeQuery(query);
		int ret = -1;
		if(re.next())
			ret = re.getInt(1);
		pre.close();
		re.close();
		return ret;
	}
	/** Returns all the entries in the CONTENTS table of the db.
	 * @return
	 * @throws SQLException 
	 */
	public List<Contents> getAllContents() throws Exception {
		String a = "SELECT * from CONTENTS;";
		Map<Integer, Peer> idtopeer = new HashMap<Integer, Peer>();
		List<Contents> ret = new ArrayList<Contents>();
		PreparedStatement pre = conn.prepareStatement(a);
		ResultSet re = pre.executeQuery();
		while(re.next()) {
			String sha1 = re.getString(1);
			int id = re.getInt(2);
			if (idtopeer.get(id) == null) {
				Peer temp = getPeer(id);
				if (temp != null)
					idtopeer.put(id, temp);

			}
			if (idtopeer.get(id) != null) {
				ret.add(new Contents(new SHA1(
						Utilities.hexStringToByteArray(sha1)),
						idtopeer.get(id).getHost(),
						idtopeer.get(id).getPort()));
			}
		}
		re.close();
		pre.close();
		return ret;
	}

	private Peer getPeer(int id) throws Exception {
		Peer ret = null;
		String a = "SELECT * from PEERINFO where ID = ?;";
		PreparedStatement pre = conn.prepareStatement(a);
		pre.setInt(1, id);
		ResultSet re = pre.executeQuery();
		if (re.next()) {
			// int dumpid = re.getInt(1);
			String host = re.getString(2);
			short port = re.getShort(3);
			ret = new Peer(Utilities.pack(
					InetAddress.getByName(host).getAddress()), port);
			if (Const.PRINTF_DB) {
				System.out.println(" [DB] (getPeer) ip: " + host + ", to int: "
						+ Utilities.pack(
								InetAddress.getByName(host).getAddress()));
			}
		}
		re.close();
		pre.close();
		return ret;
	}

	private int getPeerId(String ip, int port) throws SQLException {
		String a = "SELECT * from PEERINFO where HOST = ? and PORT = ?;";
		PreparedStatement pre = conn.prepareStatement(a);
		pre.setString(1, ip);
		pre.setInt(2, port);
		ResultSet re = pre.executeQuery();
		int peerID = -1;
		if (re.next()) {
			peerID = re.getInt(1);
		}
		pre.close();
		re.close();
		if (Const.PRINTF_DB)
			System.out.println(" [DB] " + ip + ":" + port + "-> id: " + peerID);
		return peerID;
	}

	public void insertPeer(String ip, int port) throws SQLException {
		if (getPeerId(ip, port) == -1) {
			int id = getMaxId() + 1;
			String a = "INSERT into PEERINFO values (?, ?, ?);";
			PreparedStatement pre = conn.prepareStatement(a);
			pre.setInt(1, id);
			pre.setString(2, ip);
			pre.setInt(3, port);
			pre.executeUpdate();
			pre.close();
		}
	}
	
	private boolean doIHaveHashPeer(String sha1, int peerId) throws SQLException {
		sha1 = sha1.toLowerCase();
		boolean ret = false;
		String query = "SELECT * from CONTENTS where SHA1 = ? and PEER_ID = ?;";
		PreparedStatement pre = conn.prepareStatement(query);
		pre.setString(1, sha1);
		pre.setInt(2, peerId);
		ResultSet re = pre.executeQuery();
		if (re.next())
			ret = true;
		return ret;
	}
	
	public void insertContents(String sha1, String ip, int port)
			throws SQLException
	{
		sha1 = sha1.toLowerCase();
		int peerId = getPeerId(ip, port);
		if (peerId == -1) {
			insertPeer(ip, port);
			peerId = getPeerId(ip, port);
		}
		if (peerId != -1) {
			if (!doIHaveHashPeer(sha1, peerId)) {
				String a = "INSERT into CONTENTS values (?, ?);";
				PreparedStatement pre = conn.prepareStatement(a);
				pre.setString(1, sha1);
				pre.setInt(2, peerId);
				pre.executeUpdate();
				pre.close();
			} else {
				if (Const.PRINTF_DB)
					System.out.println(" [DB] I already have the pair sha1-peer");
			}
		}

	}
	
	public List<Peer> getPeers() throws SQLException {
		List<Peer> peerList = new ArrayList<>();
		
		String a = "SELECT * FROM PEERINFO;";
		PreparedStatement pre = conn.prepareStatement(a);
		ResultSet re = pre.executeQuery();
		while(re.next()) {
			Peer peer = null;
			try {
				peer = new Peer(Utilities.pack(
						InetAddress.getByName(re.getString(2)).getAddress()),
						re.getShort(3));
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			if(peer != null) {
				peerList.add(peer);
			}
		}
		pre.close();
		re.close();
		
		return peerList; 
	}
	
	public List<Peer> getPeersWithContent(String sha1) throws SQLException {
		List<Peer> peerList = new ArrayList<>();
		sha1 = sha1.toLowerCase();
		
		String a = "SELECT I.* FROM PEERINFO I, CONTENTS C"
				+ " WHERE C.PEER_ID = I.ID AND C.SHA1 = ?";
		PreparedStatement pre = conn.prepareStatement(a);
		pre.setString(1, sha1);
		ResultSet re = pre.executeQuery();
		
		while(re.next()) {
			Peer peer = null;
			try {
				peer = new Peer(Utilities.pack(
						InetAddress.getByName(re.getString(2)).getAddress()),
						re.getShort(3));
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			if(peer != null) {
				peerList.add(peer);
			}
		}
		pre.close();
		re.close();
		
		return peerList;
	}
}
