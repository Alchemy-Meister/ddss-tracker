package tracker;

import java.awt.EventQueue;

import tracker.gui.TrackerGUI;

public class Launcher {

	public static void main(String [] args) {
		// TODO launch all the threads
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
}
