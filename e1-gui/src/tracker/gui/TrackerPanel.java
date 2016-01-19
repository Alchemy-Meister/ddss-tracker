package tracker.gui;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import common.gui.CustomJTable;
import common.gui.ObserverJPanel;
import net.miginfocom.swing.MigLayout;
import tracker.observers.FaultToleranceObserver;

public class TrackerPanel extends ObserverJPanel {

	private static final long serialVersionUID = -3635714939911877408L;
	
	private FaultToleranceObserver ftObserver;
	
	private JTable masterTable;
	private JTable slaveTable;
	
	private TrackerTableModel slaveModel, masterModel;

	public TrackerPanel(Color color) {
		super();
		this.setBackground(color);
				
		this.setLayout(new MigLayout("", "[grow][]", 
				"[][40px:n:45px][][][][][grow][grow][grow]"));
		
		JLabel lblMaster = new JLabel("Master");
		this.add(lblMaster, "cell 0 0");
		
		this.masterModel = new TrackerTableModel();
		this.masterTable = new CustomJTable(masterModel);
		masterTable.setEnabled(false);
		
		JLabel lblSlaves = new JLabel("Slaves");
		
		this.add(lblSlaves, "cell 0 5");
		
		this.slaveModel = new TrackerTableModel();
		this.slaveTable = new CustomJTable(slaveModel);
		this.slaveTable.setEnabled(false);
		
		this.add(new JScrollPane(masterTable), "cell 0 1, grow");
		this.add(new JScrollPane(slaveTable), "cell 0 7 1 2,grow");
		
		this.ftObserver = new FaultToleranceObserver();
		this.ftObserver.addObserver(this);
	}

	@Override
	public void update(Observable o, Object arg) {
		@SuppressWarnings("unchecked")
		Map<String, List<String[]>> info = (HashMap<String, List<String[]>>) arg;
		List<String[]> master = info.get("master");
		if (master != null)
			masterModel.addall(master);
		List<String[]> slaves = info.get("slaves");
		if (slaves != null)
			slaveModel.addall(slaves);
	}

	@Override
	public void unsubscribe() {
		ftObserver.rmObserver(this);
	}
}
