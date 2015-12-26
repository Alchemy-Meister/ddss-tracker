package tracker.db;

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

import bitTorrent.tracker.protocol.udp.messages.custom.LongLong;
import bitTorrent.tracker.protocol.udp.messages.custom.hi.Contents;
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
	}

	public static synchronized DBManager getInstance() {
		if (instance == null)
			instance = new DBManager();
		return instance;
	}

	public void connect() {
		if (conn != null) {
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
		Statement sta = conn.createStatement();
		String peer = "CREATE TABLE PEERINFO (" +
				" ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
				" HOST VARCHAR(255), PORT INTEGER);";
		String que = "CREATE TABLE CONTENTS (" +
				" SHA1 CHAR(40) NOT NULL PRIMARY KEY," +
				" PEER_ID INTEGER NOT NULL REFERENCES PEERINFO (SHA1) ON DELETE CASCADE"
				+ ");"; 
		sta.executeUpdate(peer);
		sta.executeUpdate(que);
		sta.close();
	}

	/** Returns all the entries in the CONTENTS table of the db.
	 * @return
	 * @throws SQLException 
	 */
	public List<Contents> getAllContents() throws SQLException {
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
				ret.add(new Contents(new LongLong(sha1.getBytes()),
						idtopeer.get(id).getHost(),
						idtopeer.get(id).getPort()));
			}
		}
		re.close();
		pre.close();
		return ret;
	}

	private Peer getPeer(int id) throws SQLException {
		Peer ret = null;
		String a = "SELECT * from PEERINFO where ID = ?;";
		PreparedStatement pre = conn.prepareStatement(a);
		pre.setInt(1, id);
		ResultSet re = pre.executeQuery();
		if (re.next()) {
			int host = re.getInt(1);
			short port = re.getShort(2);
			ret = new Peer(host, port);
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
			re.getInt(1);
		}
		pre.close();
		return peerID;
	}

	public void insertContents(String sha1, String ip, int port)
			throws SQLException
	{
		int peerId = getPeerId(ip, port);
		if (peerId != -1) {
			String a = "INSERT into CONTENTS values (?, ?);";
			PreparedStatement pre = conn.prepareStatement(a);
			pre.setString(1, sha1);
			pre.setInt(2, peerId);
			pre.executeUpdate();
			pre.close();
		}

	}
}
