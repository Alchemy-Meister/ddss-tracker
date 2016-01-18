package peer.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import common.gui.ObserverJPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTabbedPane;

/** Graphical user interface of the peer
 * @author Irene
 * @author Jesus
 */
public class PeerGUI extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5469935726172709853L;
	private JPanel contentPane;
	private JTabbedPane tabbedPane;
	
	private List<ObserverJPanel> jPanelTabList = new ArrayList<ObserverJPanel>();

	/**
	 * Create the frame.
	 */
	public PeerGUI() {
		
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
		
		jPanelTabList.add(new ConfigPanel(tabbedPanelColor));
		jPanelTabList.add(new TorrentPanel(tabbedPanelColor));
		
		tabbedPane.addTab("Configuration", null, jPanelTabList.get(0), null);
		tabbedPane.addTab("Torrents", null, jPanelTabList.get(1), null);
	
		
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