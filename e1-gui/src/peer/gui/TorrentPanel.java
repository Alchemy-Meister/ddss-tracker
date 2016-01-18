package peer.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import common.gui.CustomJTable;
import common.gui.ObserverJPanel;
import net.miginfocom.swing.MigLayout;
import tracker.Const;
import tracker.db.model.TrackerMember;
import tracker.networking.Bundle;
import tracker.observers.FaultToleranceObserver;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;

public class TorrentPanel extends ObserverJPanel implements ActionListener {
	
	private static final long serialVersionUID = -1488914219031252449L;
	
	private FaultToleranceObserver ftObserver;
	private JTable slaveTable;
	private JButton btnTorrent;
	
	private TorrentTableModel trackerModel;

	public TorrentPanel(Color color) {
		super();
		this.setBackground(color);
		
		this.setLayout(new MigLayout("", "[grow]", "[grow][grow]"));
		
		this.trackerModel = 
				new TorrentTableModel();
		
		JPanel panel = new JPanel();
		panel.setBackground(color);
		this.add(panel, "cell 0 1,grow");
		panel.setLayout(new BorderLayout(0, 0));
		
		JLabel lblTorrent = new JLabel("Torrents");
		panel.add(lblTorrent, BorderLayout.WEST);
		
		btnTorrent = new JButton("Add new");
		panel.add(btnTorrent, BorderLayout.EAST);
		
		this.slaveTable = new CustomJTable(trackerModel);
		this.slaveTable.setEnabled(false);
		this.add(new JScrollPane(slaveTable), "cell 0 6 1 2,grow");
		
		btnTorrent.addActionListener(this);
		
		this.ftObserver = new FaultToleranceObserver();
		this.ftObserver.addObserver(this);
	}

	@Override
	public void update(Observable o, Object arg) {
		//TODO CHECK FIRST TO HI MESSAGES
		@SuppressWarnings("unchecked")
		Map<String, Object> message = (HashMap<String, Object>) arg;
		if (message.get(Const.ADD_ROW) != null) {
			Bundle bundle = (Bundle) message.get(Const.ADD_ROW);
			trackerModel.addRow(bundle);
		} else {
			if (message.get(Const.DELETE_ROW) != null) {
				trackerModel.removeRow((TrackerMember) message.get(Const.DELETE_ROW));
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(btnTorrent)) {
			File newFile = null;
			
			JFileChooser fileChooser = new JFileChooser();
	        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	 
	        fileChooser.setAcceptAllFileFilterUsed(false);
	 
	        int returnValue = fileChooser.showOpenDialog(null);
	        if (returnValue == JFileChooser.APPROVE_OPTION) {
	          newFile = fileChooser.getSelectedFile();
	        }
	        if(newFile != null) {
	        	System.out.println(newFile.getName());
	        }
		}
	}
	
	@Override
	public void unsubscribe() {
		ftObserver.rmObserver(this);
	}
}