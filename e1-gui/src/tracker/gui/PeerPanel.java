package tracker.gui;

import java.awt.Color;
import java.util.Observable;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import general.components.CustomJTable;
import net.miginfocom.swing.MigLayout;
import tracker.observers.DBFaultToleranceObserver;

public class PeerPanel extends ObserverJPanel {

	private static final long serialVersionUID = -3503360364465100065L;
	
	private JTable peerListTable;
	private JTable peerInfoTable;
	private JTable peerTorrentTable;
	
	private DBFaultToleranceObserver dbFTController;
	
	private static String[] peerIDColumnNames = {"ID"};
	private static String[] peerInfoColumnNames = {"IP", "Port"};
	private static String[] torrentInfoColumnNames = {"Name", "Downloading", "Uploading", "Extra"};
	
	private Object[][] peerIDData = {
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
	
	private Object[][] peerInfoData = {{"61.24.63.40", "65122"}};
	private Object[][] torrentInfoData = {
			{"[ANK-Raws] Seitokai no Ichizon (BDrip 1920x1080 x264 FLAC Hi10P)", "35%", "2%", "u are a pirate!"},
			{"[ReinForce] Shingeki no Kyojin (BDRip 1920x1080 x264 FLAC)", "13%", "5%", "u are a pirate!"},
			{"[PSXJowie] Elfen Lied | エルフェンリート (BD 1280x720) [MP4 Batch]", "100%", "48%", "u are a pirate!"},
			{"[Leopard-Raws] CLAYMORE (BD 1920x1080 x264 AAC(Jpn+5.1eng+EngSub))", "5%", "0%", "u are a pirate!"},
			{"Hatsune MIku 39's Giving Day Concert 2010 [1080p60 Hi10p AAC + 5.1 AC3][kuchikirukia]", "74%", "23%", "u are a pirate!"},
			{"[Poi] 40mP - 小さな自分と大きな世界 feat. 初音ミク Chiisana Jibun To Ookina Sekai feat. Hatsune Miku [MP3].zip", "98%", "72%", "u are a pirate!"},
			{"[ANK-Raws] Seitokai no Ichizon (BDrip 1920x1080 x264 FLAC Hi10P)", "asd", "asd", "u are a pirate!"},
			{"[ANK-Raws] Seitokai no Ichizon (BDrip 1920x1080 x264 FLAC Hi10P)", "asd", "asd", "u are a pirate!"},
			{"[ANK-Raws] Seitokai no Ichizon (BDrip 1920x1080 x264 FLAC Hi10P)", "asd", "asd", "u are a pirate!"},
			{"[ANK-Raws] Seitokai no Ichizon (BDrip 1920x1080 x264 FLAC Hi10P)", "asd", "asd", "u are a pirate!"}
		};
	
	public PeerPanel(Color color) {
		super();
		this.setBackground(color);
		
		this.setLayout(new MigLayout("", "[grow][grow][grow][grow][grow]", "[grow][]"));
		
		JPanel peerListPanel = new JPanel();
		this.add(peerListPanel, "cell 0 0 1 2,grow");
		peerListPanel.setLayout(new MigLayout("insets 0", "[70px:75px,grow]", "[16px][grow]"));
		
		JLabel label = new JLabel("Peer list");
		peerListPanel.add(label, "cell 0 0,alignx left,aligny top");
		
		JPanel basicTorrentPanel = new JPanel();
		this.add(basicTorrentPanel, "cell 1 0 4 2,grow");
		basicTorrentPanel.setLayout(new MigLayout("insets 0", "[grow]", "[60px:n:65px,grow][grow]"));
		
		JPanel peerBasicInfoPanel = new JPanel();
		basicTorrentPanel.add(peerBasicInfoPanel, "cell 0 0,grow");
		peerBasicInfoPanel.setLayout(new MigLayout("insets 0", "[grow]", "[][grow]"));
		
		JLabel lblBasicInfo = new JLabel("Basic info");
		peerBasicInfoPanel.add(lblBasicInfo, "cell 0 0");
		
		peerInfoTable = new CustomJTable(peerInfoData, peerInfoColumnNames);
		peerInfoTable.setEnabled(false);
		peerBasicInfoPanel.add(new JScrollPane(peerInfoTable), "cell 0 1,grow");
		
		JPanel peerTorrentListPanel = new JPanel();
		basicTorrentPanel.add(peerTorrentListPanel, "cell 0 1,grow");
		peerTorrentListPanel.setLayout(new MigLayout("insets 0", "[grow]", "[][grow]"));
		
		JLabel lblTorrentList = new JLabel("Torrent list");
		peerTorrentListPanel.add(lblTorrentList, "cell 0 0");
		
		peerTorrentTable = new CustomJTable(torrentInfoData, torrentInfoColumnNames);
		peerTorrentListPanel.add(new JScrollPane(peerTorrentTable), "cell 0 1,grow");
		
		peerListTable = new CustomJTable(peerIDData, peerIDColumnNames);
		peerListTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		peerListPanel.add(new JScrollPane(peerListTable), "cell 0 1,grow");
		
		dbFTController = new DBFaultToleranceObserver();
		
		peerListTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
		      public void valueChanged(ListSelectionEvent e) {
		    	  if(!e.getValueIsAdjusting()){
		              int i = peerListTable.getSelectedRow();
		              System.out.println(peerListTable.getValueAt(i, 0));
		          }
		      }
		});
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unsubscribe() {
		dbFTController.rmObserver(this);
	}

}
