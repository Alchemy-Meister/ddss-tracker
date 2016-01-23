package peer.gui;

import java.util.LinkedHashMap;

import javax.swing.table.DefaultTableModel;

import bitTorrent.metainfo.InfoDictionarySingleFile;
import bitTorrent.metainfo.MetainfoFile;
import common.utils.Utilities;

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