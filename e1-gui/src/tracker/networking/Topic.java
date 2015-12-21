package tracker.networking;

import bitTorrent.tracker.protocol.udp.messages.custom.Type;

public enum Topic {

	KA(0), ME(1), HI(2), DS_READY(3), DS_COMMIT(4), DS_DONE(5),
	ERROR(-1);
	
	private int value;
	
	private Topic(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static Topic topicFromType(Type t) {
		switch (t.getValue()) {
		case 0:
			return Topic.KA;
		case 1:
			return Topic.ME;
		case 2:
			return Topic.HI;
		case 3:
			return Topic.DS_READY;
		case 4:
			return Topic.DS_COMMIT;
		case 5:
			return Topic.DS_DONE;
		default:
			return Topic.ERROR;
		}
	}
}