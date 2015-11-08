package tracker.networking;

import javax.swing.JLabel;

public class NetworkerStatusRunnable implements Runnable {
	
	private String[] frames = {
			"                                                         (△`ノ)",
			"                                                      (△`ノ)",
			"                                                   (△`ノ)",
			"                                                (△`ノ)",
			"                                             (△`ノ)",
			"                                          (△`ノ)",
			"                                       (△`ノ)",
			"                                    (△`ノ)",
			"                                 (△`ノ)",
			"                              (△`ノ)",
			"                           (△`ノ)",
			"                        (△`ノ)",
			"                     (△`ノ)",
			"                  (△`ノ)",
			"               (△`ノ)",
			"            (△`ノ)",
			"         (△`ノ)",
			"      (△`ノ)",
			"   (△`ノ)",
			"  (△`ノ)",
			" ヽ(   )ノ",
			"   (ヽ´△)",
			"      (ヽ´△)",
			"         (ヽ´△)",
			"            (ヽ´△)",
			"               (ヽ´△)",
			"                  (ヽ´△)",
			"                     (ヽ´△)",
			"                        (ヽ´△)",
			"                           (ヽ´△)",
			"                              (ヽ´△)",
			"                                 (ヽ´△)",
			"                                    (ヽ´△)",
			"                                       (ヽ´△)",
			"                                          (ヽ´△)",
			"                                             (ヽ´△)",
			"                                                (ヽ´△)",
			"                                                   (ヽ´△)",
			"                                                      (ヽ´△)",
			"                                                         ヽ(´△`)ﾉ"
		};
	
	private int index = 0;
	private JLabel label;
	private boolean isInterrupted = false;
	
	public NetworkerStatusRunnable() {
		
	}
	
	public void setStatusLabel(JLabel label) {
		this.label = label;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(!isInterrupted) {
			label.setText(frames[index]);
			try {
				Thread.sleep(80);
			} catch (InterruptedException e) {
				isInterrupted = true;
				label.setText("");
			}
			if(index < frames.length - 1) {
				index++;
			} else {
				index = 0;
			}
		}
	}

}
