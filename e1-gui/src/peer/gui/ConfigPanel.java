package peer.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Observable;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import common.gui.ObserverJPanel;
import net.miginfocom.swing.MigLayout;
import tracker.controllers.BasicInfoController;
import tracker.exceptions.NetProtoException;
import tracker.observers.FaultToleranceObserver;
import javax.swing.JPanel;
import java.awt.FlowLayout;

/**
 * @author Irene
 * @author Jesus
 */
public class ConfigPanel extends ObserverJPanel implements FocusListener, 
	DocumentListener, ActionListener 
{
	
	private static final long serialVersionUID = -1098424124151618936L;
	private static final String INVALID_IP_ADDRESS_MSG = "Invalid IP address";
	private static final String UNKNOWN_IP_ADDRESS_MSG = "Unknown IP address";
	private static final String INVALID_PORT_MSG = "Invalid port number";
	private static final String CONNECT_MSG = "Connect";
	private static final String DISCONNECT_MGS = "Disconnect";
	
	private JTextField tfPort;
	private JTextField tfIP;
	
	private JLabel ipAddressError;
	private JLabel portError;
	
	private JButton connectButton;
	
	private FaultToleranceObserver ftObserver;
	private BasicInfoController biController;
	
	public ConfigPanel(Color color) {
		super();
		
		ftObserver = new FaultToleranceObserver();
		ftObserver.addObserver(this);
		biController = new BasicInfoController();
		
		this.setBackground(color);
		
		this.setLayout(new MigLayout("", "[][grow][150px:n]", "[20px:n,grow][][][][20px:n,grow]"));
		
		JLabel lblIp = new JLabel("IP");
		this.add(lblIp, "cell 0 1,alignx trailing");
		
		tfIP = new JTextField();
		this.add(tfIP, "cell 1 1,growx");
		tfIP.setColumns(10);
		tfIP.addFocusListener(this);
		tfIP.getDocument().putProperty("owner", tfIP);
		tfIP.getDocument().addDocumentListener(this);
		
		JLabel lblPort = new JLabel("Port");
		this.add(lblPort, "cell 0 2,alignx trailing");
		
		tfPort = new JTextField();
		this.add(tfPort, "cell 1 2,growx");
		tfPort.setColumns(10);
		tfPort.addFocusListener(this);
		tfPort.getDocument().putProperty("owner", tfPort);
		tfPort.getDocument().addDocumentListener(this);
		
		ipAddressError = new JLabel();
		ipAddressError.setVisible(false);
		ipAddressError.setForeground(Color.RED);
		ipAddressError.setText(INVALID_IP_ADDRESS_MSG);
		this.add(ipAddressError, "cell 2 1,alignx left");
		
		portError = new JLabel(INVALID_PORT_MSG);
		portError.setVisible(false);
		portError.setForeground(Color.RED);
		this.add(portError, "cell 2 2,alignx left");
		
		JPanel panel = new JPanel();
		add(panel, "cell 1 3,grow");
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		panel.setBackground(color);
		
		connectButton = new JButton("Connect");
		connectButton.setEnabled(false);
		panel.add(connectButton);
		
		connectButton.addActionListener(this);
		
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unsubscribe() {
		ftObserver.rmObserver(this);		
	}

	@Override
	public void focusGained(FocusEvent e) {
		Component focusedComponent = e.getComponent();
		if(focusedComponent.equals(ConfigPanel.this.tfIP)) {
			if(!((JTextField) focusedComponent).getText().equals("")) {
				((JTextField) focusedComponent).selectAll();
			}
		} else if(focusedComponent.equals(ConfigPanel.this.tfPort)) {
			if(!((JTextField) focusedComponent).getText().equals("")) {
				((JTextField) focusedComponent).selectAll();
			}
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
		// NOTHING TODO HERE.
	}	

	@Override
	public void insertUpdate(DocumentEvent e) {
		showErrorMessage(e.getDocument());
		validateForm();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		showErrorMessage(e.getDocument());
		validateForm();
	}
	
	@Override
	public void changedUpdate(DocumentEvent e) {
		// NOTHING TODO HERE.
	}
	
	private void showErrorMessage(Document doc) {
		JTextField field = (JTextField) doc.getProperty("owner");
		if(field.equals(this.tfIP)) {
			try {
				if(!biController.isValidMCIP(doc.getText(0, doc.getLength()))) {
					ipAddressError.setVisible(true);
				} else {
					ipAddressError.setVisible(false);
				}
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
		} else if(field.equals(this.tfPort)) {
			try {
				if(!biController.isValidPort(doc.getText(0, doc.getLength()))) 
				{
					portError.setVisible(true);
				} else {
					portError.setVisible(false);
				}
				
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	private void validateForm() {
		boolean validForm = true;
		Component[] components = this.getComponents();
		int i = 0;
		while (validForm && i < components.length) {
	        if(components[i].getClass().equals(JLabel.class)){
	            if(components[i].equals(ipAddressError) ||
	            	components[i].equals(portError))
	            {
	            	if(components[i].isVisible()) {
		            	validForm = false;
		            }
	            }
	        }
	        i++;
	    }
		if(validForm) {
			connectButton.setEnabled(true);
		} else {
			connectButton.setEnabled(false);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton clickedButton = (JButton) e.getSource();
		if (clickedButton.equals(connectButton)) {
			if(!this.biController.isConnected()) {
				try {
					this.biController.connect(
							Integer.parseInt(this.tfPort.getText()),
							this.tfIP.getText());
					this.connectButton.setText(DISCONNECT_MGS);
					this.tfIP.setEnabled(false);
					this.tfPort.setEnabled(false);
				} catch (NetProtoException exception) {
					exception.printStackTrace();
					ipAddressError.setText(UNKNOWN_IP_ADDRESS_MSG);
					ipAddressError.setVisible(true);
					connectButton.setEnabled(false);
					tfIP.requestFocus();
				}
			} else {
				this.biController.disconnect();
				this.connectButton.setText(CONNECT_MSG);
				this.tfIP.setEnabled(true);
				this.tfPort.setEnabled(true);
			}
		}
	}
	
}