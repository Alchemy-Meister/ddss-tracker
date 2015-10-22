package tracker.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JTabbedPane;

public class TrackerGUI extends JFrame {

	private static final long serialVersionUID = -7432208373774239573L;
	
	private JPanel contentPane;
	private JTabbedPane tabbedPane;
	

	/**
	 * Create the frame.
	 */
	public TrackerGUI() {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 500, 300);
		setMinimumSize(new Dimension(500, 300));
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		Color tabbedPanelColor = new Color(230, 230, 230);
		
		JPanel basicInfoPanel = new BasicInfoPanel(tabbedPanelColor);
		JPanel trackerPanel = new TrackerPanel(tabbedPanelColor);
		JPanel peerPanel = new PeerPanel(tabbedPanelColor);
		
		tabbedPane.addTab("Basic info", null, basicInfoPanel, null);
		tabbedPane.addTab("Tracker cluster", null, trackerPanel, null);
		tabbedPane.addTab("Active peers", null, peerPanel, null);
	}

}
