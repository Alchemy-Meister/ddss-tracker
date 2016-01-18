package common.gui;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class CustomJTable extends JTable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 790923499846144660L;
	
	public CustomJTable() {
		super();
		this.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
	}
	
	public CustomJTable(Object[][] data, Object[] columnNames) {
		super(data, columnNames);
		this.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
	}
	
	public CustomJTable(DefaultTableModel model) {
		super(model);
		this.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
	}
	
	@Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component component = super.prepareRenderer(renderer, row, column);
        int rendererWidth = component.getPreferredSize().width;
        TableColumn tableColumn = getColumnModel().getColumn(column);
        tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
        return component;
     }
}
