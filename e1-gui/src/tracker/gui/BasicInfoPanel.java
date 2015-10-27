package tracker.gui;

import java.awt.Color;
import java.util.Observable;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import tracker.observers.FaultToleranceObserver;

/**
 * @author Irene
 * @author Jesus
 */
public class BasicInfoPanel extends ObserverJPanel {
	
	private static final long serialVersionUID = -1098424124151618936L;
	
	private JTextField tfID;
	private JTextField tfSP;
	private JTextField tfIP;
	private JTextField tfPP;
	
	private FaultToleranceObserver ftController;
	
	public BasicInfoPanel(Color color) {
		super();
		this.setBackground(color);
		
		this.setLayout(new MigLayout("", "[][grow][]",
				"[20px:n,grow][][][][][20px:n,grow,fill]"));
		
		JLabel lblId = new JLabel("ID");
		this.add(lblId, "cell 0 1,alignx trailing");
		
		tfID = new JTextField();
		this.add(tfID, "cell 1 1,growx");
		tfID.setColumns(10);
		
		JLabel lblIp = new JLabel("IP");
		this.add(lblIp, "cell 0 2,alignx trailing");
		
		tfIP = new JTextField();
		this.add(tfIP, "cell 1 2,growx");
		tfIP.setColumns(10);
		
		JButton startStopButton = new JButton("Provoke error");
		this.add(startStopButton, "cell 2 2 1 2");
		
		JLabel lblSwarmPort = new JLabel("Swarm port");
		this.add(lblSwarmPort, "cell 0 3");
		
		tfSP = new JTextField();
		this.add(tfSP, "cell 1 3,growx");
		tfSP.setColumns(10);
		
		JLabel lblPeerPort = new JLabel("Peer port");
		this.add(lblPeerPort, "cell 0 4,alignx trailing");
		
		tfPP = new JTextField();
		this.add(tfPP, "cell 1 4,growx");
		tfPP.setColumns(10);
		
		ftController = new FaultToleranceObserver();
		
	}
	
	public void init() {
		ftController.addObserver(this);
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unsubscribe() {
		// TODO Auto-generated method stub
		
	}

	
	
	
}