package tracker.gui;

import java.util.List;

import javax.swing.table.DefaultTableModel;

public class TrackerTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 8962817213104888132L;

	private static final String[] columnNames = new String[] {"ID", "IP",
			"Cluster port", "Latest Keepalive"};


	public TrackerTableModel() {
		super(columnNames, 0);
	}

	public static String[] getColumnNames() {
		return TrackerTableModel.columnNames;
	}

	private synchronized void cleanAll() {
		if (this.getRowCount() > 0) {
			for (int i = this.getRowCount() - 1; i >= 0; i--) {
				try {
					this.removeRow(i);
				} catch(Exception e) {};
			}
			this.fireTableDataChanged();
		}
	}

	public synchronized void addall(List<String[]> data) {
		cleanAll();
		for (String[] array : data) {
			if (array != null) {
				this.addRow(new Object[]{ array[0], array[1], array[2],
						array[3]});
			}
		}
		this.fireTableDataChanged();
	}

}