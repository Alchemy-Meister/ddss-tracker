package tracker.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import general.components.CustomJTable;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JTabbedPane;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.JButton;
import javax.swing.JTable;

public class TrackerGUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7432208373774239573L;
	private static String[] columnNames = {"ID", "IP", "Port", "Latest Keepalive"};
	private static String[] peerIDColumnNames = {"ID"};
	private static String[] peerInfoColumnNames = {"IP", "Port"};
	private static String[] torrentInfoColumnNames = {"Name", "Downloading", "Uploading", "Extra"};
	
	private Object[][] masterData = {{"0", "36.53.128.121", "5432", "2015-10-11T 10:45:32"}};
	private Object[][] slaveData = {
			{"1", "36.53.128.122", "5432", "2015-10-11T 10:45:30"},
			{"2", "36.53.128.123", "5432", "2015-10-11T 10:45:31"},
			{"3", "36.53.128.124", "5432", "2015-10-11T 10:45:32"},
			{"4", "36.53.128.125", "5432", "2015-10-11T 10:45:30"},
			{"5", "36.53.128.126", "5432", "2015-10-11T 10:45:30"},
			{"6", "36.53.128.127", "5432", "2015-10-11T 10:45:31"},
			{"7", "36.53.128.128", "5432", "2015-10-11T 10:45:32"},
			{"8", "36.53.128.129", "5432", "2015-10-11T 10:45:32"}		
		};
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
	
	private JPanel contentPane;
	private JTabbedPane tabbedPane;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTable masterTable;
	private JTable slaveTable;
	private JTable peerListTable;
	private JTable peerInfoTable;
	private JTable peerTorrentTable;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TrackerGUI frame = new TrackerGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	

	/**
	 * Create the frame.
	 */
	public TrackerGUI() {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 500, 300);
		setMinimumSize(new Dimension(500, 300));
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		Color tabbedPanelColor = new Color(230, 230, 230);
		
		JPanel basicInfoPanel = new JPanel();
		basicInfoPanel.setBackground(tabbedPanelColor);
		tabbedPane.addTab("Basic info", null, basicInfoPanel, null);
		basicInfoPanel.setLayout(new MigLayout("", "[][grow][]", "[20px:n,grow][][][][20px:n,grow,fill]"));
		
		JLabel lblId = new JLabel("ID");
		basicInfoPanel.add(lblId, "cell 0 1,alignx trailing");
		
		textField = new JTextField();
		basicInfoPanel.add(textField, "cell 1 1,growx");
		textField.setColumns(10);
		
		JLabel lblIp = new JLabel("IP");
		basicInfoPanel.add(lblIp, "cell 0 2,alignx trailing");
		
		textField_2 = new JTextField();
		basicInfoPanel.add(textField_2, "cell 1 2,growx");
		textField_2.setColumns(10);
		
		JButton startStopButton = new JButton("Provoke error");
		basicInfoPanel.add(startStopButton, "cell 2 2");
		
		JLabel lblPort = new JLabel("Port");
		basicInfoPanel.add(lblPort, "cell 0 3");
		
		textField_1 = new JTextField();
		basicInfoPanel.add(textField_1, "cell 1 3,growx");
		textField_1.setColumns(10);
		
		JPanel trackerPanel = new JPanel();
		trackerPanel.setBackground(tabbedPanelColor);
		tabbedPane.addTab("Tracker cluster", null, trackerPanel, null);
		trackerPanel.setLayout(new MigLayout("", "[grow][]", "[][40px:n:45px,grow][][][][][][grow]"));
		
		JLabel lblMaster = new JLabel("Master");
		trackerPanel.add(lblMaster, "cell 0 0");
		
		masterTable = new CustomJTable(masterData, columnNames);
		masterTable.setEnabled(false);
		trackerPanel.add(new JScrollPane(masterTable), "cell 0 1, grow");
		
		JLabel lblSlaves = new JLabel("Slaves");
		trackerPanel.add(lblSlaves, "cell 0 5");
		
		slaveTable = new CustomJTable(slaveData, columnNames);
		slaveTable.setEnabled(false);
		trackerPanel.add(new JScrollPane(slaveTable), "cell 0 6,grow");
		
		JPanel peerPanel = new JPanel();
		peerPanel.setBackground(tabbedPanelColor);
		tabbedPane.addTab("Active peers", null, peerPanel, null);
		peerPanel.setLayout(new MigLayout("", "[grow][grow][grow][grow][grow]", "[grow][]"));
		
		JPanel peerListPanel = new JPanel();
		peerPanel.add(peerListPanel, "cell 0 0 1 2,grow");
		peerListPanel.setLayout(new MigLayout("insets 0", "[70px:75px,grow]", "[16px][grow]"));
		
		JLabel label = new JLabel("Peer list");
		peerListPanel.add(label, "cell 0 0,alignx left,aligny top");
		
		peerListTable = new CustomJTable(peerIDData, peerIDColumnNames);
		peerListTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		peerListTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
		      public void valueChanged(ListSelectionEvent e) {
		    	  if(!e.getValueIsAdjusting()){
		              int i = peerListTable.getSelectedRow();
		              System.out.println(peerListTable.getValueAt(i, 0));
		          }
		      }
		});
		peerListPanel.add(new JScrollPane(peerListTable), "cell 0 1,grow");
		
		JPanel basicTorrentPanel = new JPanel();
		peerPanel.add(basicTorrentPanel, "cell 1 0 4 2,grow");
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
		
	}

}
