package tracker.gui;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import net.miginfocom.swing.MigLayout;
import tracker.Const;
import tracker.controllers.TrackerController;
import tracker.db.model.TrackerMember;
import tracker.networking.Bundle;
import tracker.observers.FaultToleranceObserver;

public class TrackerPanel extends ObserverJPanel {

	private static final long serialVersionUID = -3635714939911877408L;
	
	private FaultToleranceObserver ftObserver;
	private TrackerController trController;
	
	private JTable masterTable;
	private JTable slaveTable;
	
	private TrackerTableModel trackerModel;

	public TrackerPanel(Color color) {
		super();
		this.setBackground(color);
		
		trController = new TrackerController();
		
		this.setLayout(new MigLayout("", "[grow][]", 
				"[][40px:n:45px][][][][][grow][grow][grow]"));
		
		JLabel lblMaster = new JLabel("Master");
		this.add(lblMaster, "cell 0 0");
		
		masterTable = new CustomJTable(trController.getMasterInfo(), 
				trController.getClusterColumnNames());
		masterTable.setEnabled(false);
		
		JLabel lblSlaves = new JLabel("Slaves");
		
		this.add(lblSlaves, "cell 0 5");
		
		this.trackerModel = 
				new TrackerTableModel();
		this.slaveTable = new CustomJTable(trackerModel);
		this.slaveTable.setEnabled(false);
		
		this.add(new JScrollPane(masterTable), "cell 0 1, grow");
		this.add(new JScrollPane(slaveTable), "cell 0 7 1 2,grow");
		
		this.ftObserver = new FaultToleranceObserver();
		this.ftObserver.addObserver(this);
	}
	
	public void updateSlaveData(List<HashMap<String, String>> data) {
		
	}

	@Override
	public void update(Observable o, Object arg) {
		//TODO CHECK FIRST TO HI MESSAGES
		@SuppressWarnings("unchecked")
		Map<String, Object> message = (HashMap<String, Object>) arg;
		if (message.get(Const.ADD_ROW) != null) {
			Bundle bundle = (Bundle) message.get(Const.ADD_ROW);
			trackerModel.addRow(bundle);
		} else {
			if (message.get(Const.DELETE_ROW) != null) {
				trackerModel.removeRow((TrackerMember) message.get(Const.DELETE_ROW));
			}
		}
	}

	@Override
	public void unsubscribe() {
		ftObserver.rmObserver(this);
	}
}
