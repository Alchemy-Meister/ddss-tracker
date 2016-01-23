package peer.controllers;

import java.io.File;

import bitTorrent.metainfo.InfoDictionarySingleFile;
import bitTorrent.metainfo.MetainfoFile;
import bitTorrent.metainfo.handler.MetainfoHandlerSingleFile;
import peer.gui.TorrentTableModel;

public class TorrentController {
	
	public void addTorrent(TorrentTableModel tableModel, File torrent) {
		MetainfoHandlerSingleFile handler = new MetainfoHandlerSingleFile();
		handler.parseTorrenFile(torrent.getPath());
		
		MetainfoFile<InfoDictionarySingleFile> metainfo = handler.getMetainfo();
		
		tableModel.addRow(metainfo);
	}
}
