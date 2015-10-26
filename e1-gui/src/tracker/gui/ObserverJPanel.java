package tracker.gui;

import java.util.Observer;

import javax.swing.JPanel;

public abstract class ObserverJPanel extends JPanel implements Observer {

	private static final long serialVersionUID = 1493799617239175474L;
	
	public ObserverJPanel() {
		super();
	}
	
	public abstract void unsubscribe(); 
}
