package tracker.gui;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import net.miginfocom.swing.MigLayout;
import tracker.controllers.TrackerController;
import tracker.observers.FaultToleranceObserver;

public class TrackerPanel extends ObserverJPanel {

	private static final long serialVersionUID = -3635714939911877408L;
	
	private FaultToleranceObserver ftController;
	private TrackerController trController;
	
	private static String[] columnNames = {"ID", "IP", "Cluster port", "Peer port", "Latest Keepalive"};
	private Object[][] masterData = {{"0", "36.53.128.121", "5432", "8976", "2015-10-11T 10:45:32"}};
	private Object[][] slaveData = {
			{"1", "36.53.128.122", "5432", "8976", "2015-10-11T 10:45:30"},
			{"2", "36.53.128.123", "5432", "8976", "2015-10-11T 10:45:31"},
			{"3", "36.53.128.124", "5432", "8976", "2015-10-11T 10:45:32"},
			{"4", "36.53.128.125", "5432", "8976", "2015-10-11T 10:45:30"},
			{"5", "36.53.128.126", "5432", "8976", "2015-10-11T 10:45:30"},
			{"6", "36.53.128.127", "5432", "8976", "2015-10-11T 10:45:31"},
			{"7", "36.53.128.128", "5432", "8976", "2015-10-11T 10:45:32"},
			{"8", "36.53.128.129", "5432", "8976", "2015-10-11T 10:45:32"}		
		};
	private JTable masterTable;
	private JTable slaveTable;

	public TrackerPanel(Color color) {
		super();
		this.setBackground(color);
		
		this.setLayout(new MigLayout("", "[grow][]",
				"[][40px:n:45px,grow][][][][][][grow]"));
		
		JLabel lblMaster = new JLabel("Master");
		this.add(lblMaster, "cell 0 0");
		
		masterTable = new CustomJTable(masterData, columnNames);
		masterTable.setEnabled(false);
		
		
		JLabel lblSlaves = new JLabel("Slaves");
		this.add(lblSlaves, "cell 0 5");
		
		slaveTable = new CustomJTable(slaveData, columnNames);
		slaveTable.setEnabled(false);
		
		this.add(new JScrollPane(masterTable), "cell 0 1, grow");
		this.add(new JScrollPane(slaveTable), "cell 0 6,grow");
		
		ftController = new FaultToleranceObserver();
		ftController.addObserver(this);
	}
	
	public void updateSlaveData(List<HashMap<String, String>> data) {
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update(Observable o, Object arg) {
		updateSlaveData((List<HashMap<String, String>>) arg);
	}

	@Override
	public void unsubscribe() {
		ftController.rmObserver(this);
	}
}
