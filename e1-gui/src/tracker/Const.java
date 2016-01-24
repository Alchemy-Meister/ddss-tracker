package tracker;

import bitTorrent.tracker.protocol.udp.messages.custom.LongLong;

public class Const {

	// Debug stuff
	public static boolean CHECK_INPUTS = false;
	public static boolean PRINTF = false;
	public static boolean PRINTF_BYTES = false; // debug of bytes
	public static boolean PRINTF_IPID = true; // debug of ip-id table
	public static boolean PRINTF_FTS = true; // debug of fault tolerance system
	public static boolean PRINTF_ME = true; // debug of me system
	public static boolean PRINTF_DB = true; // debug of db manager

	// Control strings
	public static final String DELETE_ROW = "DELETE_ROW";
	public static final String ADD_ROW = "ADD_ROW";

	// Special id
	public static final LongLong UNASIGNED_ID = new LongLong("0");

	// Prefefined miliseconds
	public static final long KA_EVERY = 2000;
	public static final long HI_EVERY = 2000;
	public static final long ME_EVERY = 3000;
	public static final long WAIT_BEFORE_IAM_MASTER = 3000;

}
