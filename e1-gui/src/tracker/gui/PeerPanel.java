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
import javax.swing.table.DefaultTableModel;

import bitTorrent.tracker.protocol.udp.messages.custom.peer.AnnounceRequest;
import common.gui.CustomJTable;
import common.gui.ObserverJPanel;
import net.miginfocom.swing.MigLayout;
import tracker.controllers.PeerController;
import tracker.observers.DBFaultToleranceObserver;
import tracker.subsys.db.DBFaultToleranceSys;

public class PeerPanel extends ObserverJPanel {

	private static final long serialVersionUID = -3503360364465100065L;
	
	private JTable peerListTable;
	private JTable peerInfoTable;
	private JTable peerTorrentTable;
	
	private DBFaultToleranceObserver dbFTObserver;
	private PeerController pController;
	
	public PeerPanel(Color color) {
		super();
		
		dbFTObserver = new DBFaultToleranceObserver();
		pController = new PeerController();
		
		this.setBackground(color);
		this.setLayout(new MigLayout("", "[grow][grow][grow][grow][grow]",
				"[grow][]"));
		
		JPanel peerListPanel = new JPanel();
		this.add(peerListPanel, "cell 0 0 1 2,grow");
		peerListPanel.setLayout(new MigLayout("insets 0", "[70px:75px,grow]",
				"[16px][grow]"));
		peerListPanel.setBackground(color);
		
		JLabel label = new JLabel("Peer list");
		peerListPanel.add(label, "cell 0 0,alignx left,aligny top");
		
		JPanel basicTorrentPanel = new JPanel();
		this.add(basicTorrentPanel, "cell 1 0 4 2,grow");
		basicTorrentPanel.setLayout(new MigLayout("insets 0", "[grow]",
				"[60px:n:65px,grow][grow]"));
		basicTorrentPanel.setBackground(color);
		
		JPanel peerBasicInfoPanel = new JPanel();
		basicTorrentPanel.add(peerBasicInfoPanel, "cell 0 0,grow");
		peerBasicInfoPanel.setLayout(new MigLayout("insets 0", "[grow]",
				"[][grow]"));
		peerBasicInfoPanel.setBackground(color);
		
		JLabel lblBasicInfo = new JLabel("Basic info");
		peerBasicInfoPanel.add(lblBasicInfo, "cell 0 0");
		peerBasicInfoPanel.setBackground(color);
		
		peerInfoTable = new CustomJTable();
		
		
		peerInfoTable.setModel(new DefaultTableModel(
				pController.getPeerBasicHardCodedInfo(), 
				pController.getPeerInfoColumnNames()));
		((DefaultTableModel) peerInfoTable.getModel()).setRowCount(0);
		peerInfoTable.setEnabled(false);
		peerBasicInfoPanel.add(new JScrollPane(peerInfoTable), "cell 0 1,grow");
		
		JPanel peerTorrentListPanel = new JPanel();
		basicTorrentPanel.add(peerTorrentListPanel, "cell 0 1,grow");
		peerTorrentListPanel.setLayout(new MigLayout("insets 0", "[grow]",
				"[][grow]"));
		peerTorrentListPanel.setBackground(color);
		
		JLabel lblTorrentList = new JLabel("Torrent list");
		peerTorrentListPanel.add(lblTorrentList, "cell 0 0");
		
		peerTorrentTable = new CustomJTable();
		peerTorrentTable.setEnabled(false);
		peerTorrentTable.setModel(new DefaultTableModel(
				pController.getPeerTorrentsHardCodedData(),
				pController.getPeerTorrentColumnNames()));
		((DefaultTableModel) peerTorrentTable.getModel()).setRowCount(0);
		peerTorrentListPanel.add(new JScrollPane(peerTorrentTable),
				"cell 0 1,grow");
		
		DefaultTableModel tm = new DefaultTableModel(
				pController.getPeerListHardCodedData(),
				pController.getPeerIDListColumnName());
		peerListTable = new CustomJTable(tm);
		peerListTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		peerListPanel.add(new JScrollPane(peerListTable), "cell 0 1,grow");
		
		DBFaultToleranceSys.getInstance().addObserver(this);
		
		peerListTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e) {
				if(!e.getValueIsAdjusting()) {
					String peerID = peerListTable.getValueAt(
						peerListTable.getSelectedRow(), 0).toString();
		            
					String[][] peerInfo = pController.getPeerBasicData(peerID);
		            DefaultTableModel peerInfoModel = 
		            		(DefaultTableModel) peerInfoTable.getModel();
		            peerInfoModel.setRowCount(0);
		            for(int i = 0; i < peerInfo.length; i++) {
		            	peerInfoModel.addRow(peerInfo[i]);
		            }
		            
		            String[][] peerTorrents = 
		            		pController.getPeerTorrents(peerID);
		            DefaultTableModel torrentModel = (DefaultTableModel)
		            		peerTorrentTable.getModel();
		            torrentModel.setRowCount(0);
		            for(int i= 0; i < peerTorrents.length; i++) {
		            	torrentModel.addRow(peerTorrents[i]);
		            }
		        }
			}
		});
	}

	@Override
	public void update(Observable o, Object arg) {
		if(arg instanceof AnnounceRequest) {
			updateIDs();
		}
	}
	
	public synchronized void updateIDs() {
		DefaultTableModel model = (DefaultTableModel)peerListTable.getModel();
		if (model.getRowCount() > 0) {
			for (int i = model.getRowCount() - 1; i >= 0; i--) {
				model.removeRow(i);
			}
			model.fireTableDataChanged();
		}
		String[][] ids = this.pController.getPeerListHardCodedData();
		for(int i = 0; i < ids.length; i++) {
			model.addRow(new Object[] {ids[i][0]});
		}
		model.fireTableDataChanged();
	}

	@Override
	public void unsubscribe() {
		dbFTObserver.rmObserver(this);
	}

}
