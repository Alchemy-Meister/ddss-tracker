package tracker.networking;

public enum Topic {

	KA(0), ME(1), HI(2), DS_READY(3), DS_COMMIT(4), DS_DONE(5);
	
	private int value;
	
	private Topic(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
}
