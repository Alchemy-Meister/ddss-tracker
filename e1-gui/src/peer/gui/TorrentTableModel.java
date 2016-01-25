package peer.gui;

import java.util.LinkedHashMap;

import javax.swing.table.DefaultTableModel;

import bitTorrent.metainfo.InfoDictionarySingleFile;
import bitTorrent.metainfo.MetainfoFile;
import common.utils.Utilities;
import peer.model.Torrent;

public class TorrentTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 8962817213104888132L;

	private LinkedHashMap<String, MetainfoFile<InfoDictionarySingleFile>> data;

	private static final String[] columnNames = new String[] {"File name", 
			"Hash", "Download %", "Upload %", "Leechers", "Seeders"};


	public TorrentTableModel() {
		super(columnNames, 0);
		data = new LinkedHashMap<String, MetainfoFile<InfoDictionarySingleFile>>();
	}

	public static String[] getColumnNames() {
		return TorrentTableModel.columnNames;
	}
	
	public void updateTorrent(String hash, Torrent torrent) {
		boolean found = false;
		for (int i = 0; i < this.getRowCount() && !found; i++) {
			String rowHash = (String) this.getValueAt(i, 1);
			if(rowHash.equals(hash.toLowerCase())) {
				this.setValueAt(torrent.getLeechers(), i, 4);
				this.setValueAt(torrent.getSeeders(), i, 5);
				this.fireTableDataChanged();
				found = true;
			}
		}
	}

	public void addRow(MetainfoFile<InfoDictionarySingleFile> torrent) {		
	    String hexInfoHash = 
	    		Utilities.toHexString(torrent.getInfo().getInfoHash());
		MetainfoFile<InfoDictionarySingleFile> old = data.put(
	    		hexInfoHash, torrent);
		
	    if (old == null) {
	    	this.addRow(new Object[]{
					torrent.getInfo().getName(),
					hexInfoHash, 
					0,
					0,
					0,
					0});
			this.fireTableDataChanged();
		}
	}
}