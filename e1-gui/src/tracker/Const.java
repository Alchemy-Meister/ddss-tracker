package tracker;

import bitTorrent.tracker.protocol.udp.messages.custom.LongLong;

public class Const {

	// Debug stuff
	public static boolean CHECK_INPUTS = false;
	public static boolean PRINTF = false;
	public static boolean PRINTF_BYTES = false; // debug of bytes
	public static boolean PRINTF_IPID = false; // debug of ip-id table
	public static boolean PRINTF_FTS = true; // debug of fault tolerance system
	public static boolean PRINTF_ME = true; // debug of me system
	public static boolean PRINTF_DB = true; // debug of db manager
	public static boolean PRINTF_DBFTS = true; // debug of db fault tole. sys.
	public static boolean PRINTF_DISPA = true; // debug of dispatcher

	// Control strings
	public static final String DELETE_ROW = "DELETE_ROW";
	public static final String ADD_ROW = "ADD_ROW";

	// Special id
	public static final LongLong UNASIGNED_ID = new LongLong("0");
	
	// enable udp or jms
	public static final boolean ENABLE_JMS = true;
	public static final String JMS_HOST = "127.0.0.1";
	public static final int JMS_PORT = 61616;
	public static final String CONN_FACT_NAME = "TopicConnectionFactory";
	public static final String SUBSCRIBER_ID = "SubscriberID";

	// Prefefined miliseconds
	public static final long KA_EVERY = 2000;
	public static final long HI_EVERY = 2000;
	public static final long ME_EVERY = 1000;
	public static final long WAIT_BEFORE_IAM_MASTER = 3000;
	
	// retry waits
	// if no ds-ready is listened in ANNOUNCE_RETRY ms the announce is resend 
	public static final long ANNOUNCE_RETRY = 10000;
	// if no ds-commit is listened in DS_COMMIT_RETRY ms the commitis resend
	public static final long DS_COMMIT_RETRY = 10000;
}