package tracker.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/** Graphical user interface of the tracker
 * @author Irene
 * @author Jesus
 */
public class TrackerGUI extends JFrame {

	private static final long serialVersionUID = -7432208373774239573L;
	
	private JPanel contentPane;
	private JTabbedPane tabbedPane;
	
	private List<ObserverJPanel> jPanelTabList = new ArrayList<ObserverJPanel>();

	/**
	 * Create the frame.
	 */
	public TrackerGUI() {
		// check os to apply look and feel
		if (System.getProperty("os.name").toLowerCase().contains("linux")) {
			try {
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk." +
						"GTKLookAndFeel");
			} catch (ClassNotFoundException | InstantiationException
					| IllegalAccessException
					| UnsupportedLookAndFeelException e) {
				e.printStackTrace();
			}
		}
		
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setBounds(100, 100, 600, 300);
		this.setMinimumSize(new Dimension(600, 300));
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setContentPane(contentPane);
		this.contentPane.setLayout(new BorderLayout(0, 0));
		
		this.tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		this.contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		Color tabbedPanelColor = new Color(230, 230, 230);
		
		jPanelTabList.add(new BasicInfoPanel(tabbedPanelColor));
		jPanelTabList.add(new TrackerPanel(tabbedPanelColor));
		jPanelTabList.add(new PeerPanel(tabbedPanelColor));
		
		tabbedPane.addTab("Basic info", null, jPanelTabList.get(0), null);
		tabbedPane.addTab("Tracker cluster", null, jPanelTabList.get(1), null);
		tabbedPane.addTab("Active peers", null, jPanelTabList.get(2), null);
	
		
		this.addWindowListener(new java.awt.event.WindowAdapter() {
		    
			@Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				for (int i = 0; i < jPanelTabList.size(); i++) {
					jPanelTabList.get(i).unsubscribe();
				}
				System.exit(0);
			}
		});
	}

}
