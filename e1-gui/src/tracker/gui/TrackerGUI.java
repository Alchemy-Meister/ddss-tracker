package tracker.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JTabbedPane;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;

public class TrackerGUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7432208373774239573L;
	private JPanel contentPane;
	private JTabbedPane tabbedPane;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TrackerGUI frame = new TrackerGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public TrackerGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setMinimumSize(new Dimension(450, 300));
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		Color tabbedPanelColor = new Color(230, 230, 230);
		
		JPanel basicInfoPanel = new JPanel();
		basicInfoPanel.setBackground(tabbedPanelColor);
		tabbedPane.addTab("Basic info", null, basicInfoPanel, null);
		basicInfoPanel.setLayout(new MigLayout("", "[][grow][]", "[20px:n,fill][20px:n,fill][][][][20px:n,fill][fill]"));
		
		JLabel lblId = new JLabel("ID");
		basicInfoPanel.add(lblId, "cell 0 2,alignx trailing");
		
		textField = new JTextField();
		basicInfoPanel.add(textField, "cell 1 2,growx");
		textField.setColumns(10);
		
		JLabel lblIp = new JLabel("IP");
		basicInfoPanel.add(lblIp, "cell 0 3,alignx trailing");
		
		textField_2 = new JTextField();
		basicInfoPanel.add(textField_2, "cell 1 3,growx");
		textField_2.setColumns(10);
		
		JButton startStopButton = new JButton("Shut down");
		basicInfoPanel.add(startStopButton, "cell 2 3");
		
		JLabel lblPort = new JLabel("Port");
		basicInfoPanel.add(lblPort, "cell 0 4");
		
		textField_1 = new JTextField();
		basicInfoPanel.add(textField_1, "cell 1 4,growx");
		textField_1.setColumns(10);
		
		JPanel trackerListPanel = new JPanel();
		trackerListPanel.setBackground(tabbedPanelColor);
		tabbedPane.addTab("Tracker swarm", null, trackerListPanel, null);
		
		JPanel peerListPanel = new JPanel();
		peerListPanel.setBackground(tabbedPanelColor);
		tabbedPane.addTab("Active peers", null, peerListPanel, null);
	}

}
