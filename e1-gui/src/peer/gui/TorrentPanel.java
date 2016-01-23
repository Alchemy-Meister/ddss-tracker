package peer.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Observable;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;

import common.gui.CustomJTable;
import common.gui.ObserverJPanel;
import net.miginfocom.swing.MigLayout;
import peer.controllers.TorrentController;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;

public class TorrentPanel extends ObserverJPanel implements ActionListener {
	
	private static final long serialVersionUID = -1488914219031252449L;
	
	private JTable slaveTable;
	private JButton btnTorrent;
	
	private TorrentTableModel torrentModel;
	
	private TorrentController controller;

	public TorrentPanel(Color color) {
		super();
		this.setBackground(color);
		
		this.setLayout(new MigLayout("", "[grow]", "[grow][grow]"));
		
		this.torrentModel = 
				new TorrentTableModel();
		
		JPanel panel = new JPanel();
		panel.setBackground(color);
		this.add(panel, "cell 0 1,grow");
		panel.setLayout(new BorderLayout(0, 0));
		
		JLabel lblTorrent = new JLabel("Torrents");
		panel.add(lblTorrent, BorderLayout.WEST);
		
		btnTorrent = new JButton("Add new");
		panel.add(btnTorrent, BorderLayout.EAST);
		
		this.slaveTable = new CustomJTable(this.torrentModel);
		this.slaveTable.setEnabled(false);
		this.add(new JScrollPane(slaveTable), "cell 0 6 1 2,grow");
		
		btnTorrent.addActionListener(this);
		
		controller = new TorrentController();
	}

	@Override
	public void update(Observable o, Object arg) {
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(btnTorrent)) {
			JFileChooser fileChooser = new JFileChooser();
	        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	        FileNameExtensionFilter filter = new FileNameExtensionFilter(
	        		"Torrent", "torrent", "torrent");
	        fileChooser.setAcceptAllFileFilterUsed(false);
	        fileChooser.setFileFilter(filter);
	 
	        int returnValue = fileChooser.showOpenDialog(null);
	        if (returnValue == JFileChooser.APPROVE_OPTION) {
	          File torrent = fileChooser.getSelectedFile();
	          controller.addTorrent(this.torrentModel, torrent);
	        }
		}
	}

	@Override
	public void unsubscribe() {
		
	}
}