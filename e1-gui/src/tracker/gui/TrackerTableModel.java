package tracker.gui;

import java.util.LinkedHashMap;

import javax.swing.table.DefaultTableModel;

import bitTorrent.tracker.protocol.udp.messages.custom.LongLong;
import bitTorrent.tracker.protocol.udp.messages.custom.ka.KeepAliveM;
import tracker.db.model.TrackerMember;
import tracker.networking.Bundle;

public class TrackerTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 8962817213104888132L;

	private LinkedHashMap<String, Bundle> data;

	private static final String[] columnNames = new String[] {"ID", "IP",
			"Cluster port", "Latest Keepalive"};


	public TrackerTableModel() {
		super(columnNames, 0);
		data = new LinkedHashMap<String, Bundle>();
	}

	public static String[] getColumnNames() {
		return TrackerTableModel.columnNames;
	}

	/** Finds an id in the table. -1 if not found
	 * @param idToFind
	 * @return
	 */
	private int findRowIndex(LongLong idToFind) {
		int ret = -1;
		boolean found = false;
		for(int i = 0; !found && i < this.getRowCount(); i++) {
			if(((LongLong) this.getValueAt(i, 0)).equals(idToFind)) {
				found = true;
				ret = i;
			}
		}
		return ret;
	}

	public void removeRow(TrackerMember member) {
		int row = findRowIndex(member.getId());
		if (row != -1) {
			this.removeRow(row);
			this.fireTableDataChanged();
		}
	}

	public void addRow(Bundle bundle) {
		Bundle old = data.put(
				((KeepAliveM) bundle.getMessage()).getId().toString(), bundle);
		if (old == null) {
			this.addRow(new Object[]{
					((KeepAliveM) bundle.getMessage()).getId(),
					bundle.getIP(), 
					bundle.getPort(),
					bundle.getTimestamp()});
			this.fireTableDataChanged();
		} else {
			int row = findRowIndex(((KeepAliveM) bundle.getMessage()).getId());
			if (row != -1) {
				this.removeRow(row);
				this.insertRow(row, new Object[]{
						((KeepAliveM) bundle.getMessage()).getId(),
						bundle.getIP(), 
						bundle.getPort(),
						bundle.getTimestamp()});
				this.fireTableDataChanged();
			}
		}
	}
}