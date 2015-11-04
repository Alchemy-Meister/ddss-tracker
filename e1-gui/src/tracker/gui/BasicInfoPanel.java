package tracker.gui;

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

import net.miginfocom.swing.MigLayout;
import tracker.controllers.BasicInfoController;
import tracker.observers.FaultToleranceObserver;
import javax.swing.JPanel;
import java.awt.FlowLayout;

/**
 * @author Irene
 * @author Jesus
 */
public class BasicInfoPanel extends ObserverJPanel implements FocusListener, 
	DocumentListener, ActionListener 
{
	
	private static final long serialVersionUID = -1098424124151618936L;
	private static final String INVALID_IP_ADDRESS_MSG = "Invalid IP address";
	private static final String INVALID_PORT_MSG = "Invalid port number";
	private static final String SAME_PORT_ERROR_MSG = "Ports can't be the same";
	private static final String CONNECT_MSG = "Connect";
	private static final String DISCONNECT_MGS = "Disconnect";
	
	private JTextField tfID;
	private JTextField tfSP;
	private JTextField tfIP;
	private JTextField tfPP;
	
	private JLabel ipAddressError;
	private JLabel peerPortError;
	private JLabel clusterPortError;
	
	private JLabel panicLabel;
	
	private JButton connectButton;
	private JButton errorButton;
	
	private FaultToleranceObserver ftObserver;
	private BasicInfoController biController;
	
	public BasicInfoPanel(Color color) {
		super();
		
		ftObserver = new FaultToleranceObserver();
		ftObserver.addObserver(this);
		biController = new BasicInfoController();
		
		this.setBackground(color);
		
		this.setLayout(new MigLayout("", "[][grow][150px:n]", "[20px:n,grow][][][][][][20px:n,grow]"));
		
		JLabel lblId = new JLabel("ID");
		this.add(lblId, "cell 0 1,alignx trailing");
		
		tfID = new JTextField();
		tfID.setEnabled(false);
		this.add(tfID, "cell 1 1,growx");
		tfID.setColumns(10);
		
		JLabel lblIp = new JLabel("IP");
		this.add(lblIp, "cell 0 2,alignx trailing");
		
		tfIP = new JTextField();
		this.add(tfIP, "cell 1 2,growx");
		tfIP.setColumns(10);
		tfIP.addFocusListener(this);
		tfIP.getDocument().putProperty("owner", tfIP);
		tfIP.getDocument().addDocumentListener(this);
		
		JLabel lblSwarmPort = new JLabel("Cluster port");
		this.add(lblSwarmPort, "cell 0 3");
		
		tfSP = new JTextField();
		this.add(tfSP, "cell 1 3,growx");
		tfSP.setColumns(10);
		tfSP.addFocusListener(this);
		tfSP.getDocument().putProperty("owner", tfSP);
		tfSP.getDocument().addDocumentListener(this);
		
		JLabel lblPeerPort = new JLabel("Peer port");
		this.add(lblPeerPort, "cell 0 4,alignx trailing");
		
		tfPP = new JTextField();
		this.add(tfPP, "cell 1 4,growx");
		tfPP.setColumns(10);
		tfPP.addFocusListener(this);
		tfPP.getDocument().putProperty("owner", tfPP);
		tfPP.getDocument().addDocumentListener(this);
		
		ipAddressError = new JLabel();
		ipAddressError.setVisible(false);
		ipAddressError.setForeground(Color.RED);
		ipAddressError.setText(INVALID_IP_ADDRESS_MSG);
		this.add(ipAddressError, "cell 2 2,alignx left");
		
		clusterPortError = new JLabel(INVALID_PORT_MSG);
		clusterPortError.setVisible(false);
		clusterPortError.setForeground(Color.RED);
		this.add(clusterPortError, "cell 2 3,alignx left");
		
		peerPortError = new JLabel(INVALID_PORT_MSG);
		peerPortError.setVisible(false);
		peerPortError.setForeground(Color.RED);
		this.add(peerPortError, "cell 2 4");
		
		JPanel panel = new JPanel();
		add(panel, "cell 1 5,grow");
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		panel.setBackground(color);
		
		errorButton = new JButton("Provoke error");
		errorButton.setEnabled(false);
		panel.add(errorButton);
		
		connectButton = new JButton("Connect");
		connectButton.setEnabled(false);
		panel.add(connectButton);
		
		connectButton.addActionListener(this);
		errorButton.addActionListener(this);
		
		JPanel panicPanel = new JPanel();
		panicPanel.setBackground(color);
		this.add(panicPanel, "cell 1 6,grow");
		panicPanel.setLayout(null);
		
		panicLabel = new JLabel("");
		panicLabel.setBounds(0, 0, 318, 20);
		panicPanel.add(panicLabel);
		
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
		if(focusedComponent.equals(BasicInfoPanel.this.tfIP)) {
			if(!((JTextField) focusedComponent).getText().equals("")) {
				((JTextField) focusedComponent).selectAll();
			}
		} else if(focusedComponent.equals(BasicInfoPanel.this.tfSP)) {
			if(!((JTextField) focusedComponent).getText().equals("")) {
				((JTextField) focusedComponent).selectAll();
			}
		} else if(focusedComponent.equals(BasicInfoPanel.this.tfPP)) {
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
				if(!biController.isValidIP(doc.getText(0, doc.getLength()))) {
					ipAddressError.setVisible(true);
				} else {
					ipAddressError.setVisible(false);
				}
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
		} else if(field.equals(this.tfSP)) {
			try {
				if(!biController.isValidPort(doc.getText(0, doc.getLength()))) 
				{
					clusterPortError.setText(INVALID_PORT_MSG);
					clusterPortError.setVisible(true);
				} else if(this.tfSP.getText().equals(this.tfPP.getText())){
					clusterPortError.setText(SAME_PORT_ERROR_MSG);
					clusterPortError.setVisible(true);
				} else {
					clusterPortError.setVisible(false);
				}
				if(!this.tfSP.getText().equals(this.tfPP.getText())) {
					if(peerPortError.isVisible()) {
						if(biController.isValidPort(this.tfPP.getText())) {
							peerPortError.setVisible(false);
						} else {
							peerPortError.setText(INVALID_PORT_MSG);
							peerPortError.setVisible(true);
						}
					}
				}
				
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
		} else if(field.equals(this.tfPP)) {
			try {
				if(!biController.isValidPort(doc.getText(0, doc.getLength()))) 
				{
					peerPortError.setText(INVALID_PORT_MSG);
					peerPortError.setVisible(true);
				} else if(this.tfSP.getText().equals(this.tfPP.getText())){
					peerPortError.setText(SAME_PORT_ERROR_MSG);
					peerPortError.setVisible(true);
				}  else {
					peerPortError.setVisible(false);
				}
				if(!this.tfSP.getText().equals(this.tfPP.getText())) {
					if(clusterPortError.isVisible()) {
						if(biController.isValidPort(this.tfSP.getText())) {
							clusterPortError.setVisible(false);
						} else {
							clusterPortError.setText(INVALID_PORT_MSG);
							clusterPortError.setVisible(true);
						}
					}
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
	            	components[i].equals(peerPortError) ||
	            	components[i].equals(clusterPortError))
	            {
	            	if(components[i].isVisible()) {
		            	validForm = false;
		            }
	            }
	        } else if(components[i].getClass().equals(JTextField.class)){
	        	if(((JTextField)components[i]).getText().equals("")) {
	        		if(!components[i].equals(tfID)) {
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
		if(clickedButton.equals(connectButton)) {
			System.out.println("Connect click （*´▽｀*）");
			if(!this.biController.isConnected()) {
				this.biController.connect(Integer.parseInt(this.tfSP.getText()),
						this.tfIP.getText(), this.panicLabel);
				this.connectButton.setText(DISCONNECT_MGS);
			} else {
				this.biController.disconnect();
				this.connectButton.setText(CONNECT_MSG);
			}
		} else if(clickedButton.equals(errorButton)) {
			System.out.println("Hey what are you trying to do, "
					+ "don't be a meanie <(｀^´)>");
		}
		
	}
}