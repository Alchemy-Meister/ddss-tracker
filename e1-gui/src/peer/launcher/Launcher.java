package peer.launcher;

import java.awt.EventQueue;

import peer.gui.PeerGUI;

public class Launcher {

	public static void main(String [] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PeerGUI frame = new PeerGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
