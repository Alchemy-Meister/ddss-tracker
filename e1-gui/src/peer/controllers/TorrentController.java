package peer.controllers;

import java.io.File;

import bitTorrent.metainfo.InfoDictionarySingleFile;
import bitTorrent.metainfo.MetainfoFile;
import bitTorrent.metainfo.handler.MetainfoHandlerSingleFile;
import peer.gui.TorrentTableModel;
import peer.model.Torrent;

public class TorrentController {
	
	public void addTorrent(TorrentTableModel tableModel, File torrent) {
		MetainfoHandlerSingleFile handler = new MetainfoHandlerSingleFile();
		handler.parseTorrenFile(torrent.getPath());
		
		MetainfoFile<InfoDictionarySingleFile> metainfo = handler.getMetainfo();
		Torrent.torrents.put(metainfo.getInfo().getHexInfoHash(),
				new Torrent(metainfo.getInfo().getLength()));
		
		tableModel.addRow(metainfo);
	}
}
