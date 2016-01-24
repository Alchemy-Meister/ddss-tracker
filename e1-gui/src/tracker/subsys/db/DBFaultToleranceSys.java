package tracker.subsys.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import bitTorrent.tracker.protocol.udp.messages.custom.LongLong;
import bitTorrent.tracker.protocol.udp.messages.custom.SHA1;
import bitTorrent.tracker.protocol.udp.messages.custom.ds.DSCommitM;
import bitTorrent.tracker.protocol.udp.messages.custom.ds.DSDoneM;
import bitTorrent.tracker.protocol.udp.messages.custom.ds.DSReadyM;
import bitTorrent.tracker.protocol.udp.messages.custom.peer.AnnounceRequest;
import common.utils.Utilities;
import tracker.Const;
import tracker.db.DBManager;
import tracker.db.model.TrackerMember;
import tracker.networking.Bundle;
import tracker.networking.Topic;
import tracker.subsys.TrackerSubsystem;
import tracker.subsys.cfts.IpIdTable;

/** The db replication is handled in this class.
 * @author Irene
 * @author Jesus
 */
public class DBFaultToleranceSys extends TrackerSubsystem implements Runnable {

	private static DBFaultToleranceSys instance = null;
	private IpIdTable ipidtable = null;
	
	private DBManager manager;
	
	// id transaction_id - key announce request original message
	// waiting anno. requests
	private Map<Integer, AnnounceRequest> waitingAR;
	// order of the waiting anno. requests
	private List<Integer> waitingAROrder;
	// when the anno. r. was received
	private Map<Integer, Long> waitingARTime; 
	// lock for the above variables
	private final Lock announceOrderLock = new ReentrantLock();
	
	// id of peers ready for transaction_id
	private Map<Integer, List<String>> transIdReady;
	private final Lock transIdReadyLock = new ReentrantLock();
	
	// When all the instances aren't ready we check in the run() loop
	// if we have to manually send the announce that the peer sent before
	private final Lock checkTimeToRetryAnnounceLock = new ReentrantLock();
	private boolean checkTimeToRetryAnnounce = false;
	
	// id of peers ho have commited a given transaction id
	private Map<Integer, List<String>> transIdCommited;
	private final Lock transIdCommitedLock = new ReentrantLock();
	
	// When all the instances do not send a commit done we check in the run()
	// loop if we have to resend the commit order
	private final Lock checkTimeToRetryCommitLock = new ReentrantLock();
	private boolean checkTimeToRetryCommit = false;
	
	// When we commit a transaction we store it here to avoid duplication
	private List<Integer> alreadyCommited = null;
	private final Lock alreadyCommitedLock = new ReentrantLock();
	
	private DBFaultToleranceSys() {
		super();
		this.manager = DBManager.getInstance(); // tables created at fts
		this.ipidtable = IpIdTable.getInstance();
		this.waitingAR = new HashMap<Integer, AnnounceRequest>();
		this.waitingAROrder = new ArrayList<Integer>();
		this.alreadyCommited = new ArrayList<Integer>();
		this.transIdReady = new HashMap<Integer, List<String>>();
	}
	
	/** Singleton. Just one thread.
	 * @return
	 */
 	public static DBFaultToleranceSys getInstance() {
		if (instance == null)
			instance = new DBFaultToleranceSys();
		return instance;
	}
	
	@Override
	public void run() {
		TrackerSubsystem.networker.subscribe(Topic.ANNOUNCE_R, this);
		TrackerSubsystem.networker.subscribe(Topic.DS_READY, this);
		TrackerSubsystem.networker.subscribe(Topic.DS_COMMIT, this);
		TrackerSubsystem.networker.subscribe(Topic.DS_DONE, this);
		
		while(true) {
			if (canIParticipate()) {
				if (getCheckTimeToRetryAnnounce()) {
					// TODO
					
				}
				if (getCheckTimeToRetryCommit()) {
					
				}
			}
			
			
			try { Thread.sleep(100);} catch (Exception e) {}
		}
	}
	
	private boolean canIParticipate() {
		return false; // TODO
	}

	@Override
	public void receive(Topic topic, Bundle bundle) {
		if (canIParticipate()) {
			if (topic == Topic.ANNOUNCE_R) {
				AnnounceRequest announce = bundle.getPeerMessage();
				putAR(announce);
				if (ipidtable.amIMaster()) {
					// initialise the list of peers ready for this tran id
					transIdReadyLock.lock();
					try {
						if (!transIdReady.containsKey(
								announce.getTransactionId()))
						{
							transIdReady.put(announce.getTransactionId(),
									new ArrayList<String>());
						}
					} finally {
						transIdReadyLock.unlock();
					}
				} else {
					
					// send a DS_READY if I am ready to commit
					try {
						List<SHA1> info_hashes = new ArrayList<SHA1>();
						info_hashes.add(
								new SHA1(Utilities.hexStringToByteArray(
										announce.getInfoHash())));
						LongLong myid = ipidtable.getMyId();
						if (myid != null && !myid.toString().equals(
								Const.UNASIGNED_ID.toString()))
						{
							DSReadyM dsready = new DSReadyM(
									announce.getConnectionId(),
									announce.getAction().value(),
									announce.getTransactionId(),
									myid, info_hashes);
							networker.publish(Topic.DS_READY, dsready);
						} else {
							// If my id is not set I dont have to send ready.
							// If I have and id but the below exception is
							// thrown the master will send the announce again.
						}
					} catch (Exception e) {
						// the sha is corrupt, delete it
						removeAR(announce.getTransactionId());
					}
				}
			} else if (topic == Topic.DS_READY) {
				DSReadyM dsready = (DSReadyM) bundle.getMessage();
				if (ipidtable.amIMaster()) {
					// update the list of peers ready for the given trans id
					String id = dsready.getId().toString();
					if (!id.equals(ipidtable.getMyId().toString())) {
						setTransIdReady(dsready.getTransactionId(), id);
						// after updating check if we have them all to send 
						// a DS_COMMIT
						if (haveThemAllReady(dsready.getTransactionId())) {
							setCheckTineToRetryAnnounce(false);
							AnnounceRequest ar = getAR(
									dsready.getTransactionId());
							if (ar != null) {
								DSCommitM commit = new DSCommitM(
										dsready.getConnection_id(),
										ar.getAction().value(),
										dsready.getTransactionId());
								networker.publish(Topic.DS_COMMIT, commit);
							}
						} else {
							// The other thread checks if we have to send the
							// announce again or not
							setCheckTineToRetryAnnounce(true);
						}
					}
				}
			} else if (topic == Topic.DS_COMMIT) {
				// Master and slaves commit at the same time
				DSCommitM commit = (DSCommitM) bundle.getMessage();
				AnnounceRequest an = getAR(commit.getTransactionId());
				// Check whether we have commited such transaction id yet
				if (!haveIAlreadyCommited(commit.getTransactionId())) {
					manager.connect();
					try {
						manager.insertContents(an.getInfoHash(),
								new String(Utilities.unpack(
										an.getPeerInfo().getIpAddress())),
								an.getPeerInfo().getPort());
						putIHaveAlreadyCommited(commit.getTransactionId());
					} catch (SQLException e) {
						e.printStackTrace(System.err);
					}
					manager.disconnect();
				}
				// either way, send that the transaction is done
				// (if we are not master)
				if (!ipidtable.amIMaster()) {
					LongLong myid = ipidtable.getMyId();
					if (myid != null) {
						networker.publish(Topic.DS_DONE, 	
						new DSDoneM(commit.getTransactionId(), myid));
					}
				}
			} else if (topic == Topic.DS_DONE) {
				DSDoneM done = (DSDoneM) bundle.getMessage();
				if (ipidtable.amIMaster()) {
					setTransIdCommited((int) done.getConnection_id(),
							done.getSenderId().toString());
				}
			}
		}
	}
	
	// Track the slave ids that have a given transaction id ready
	private void setTransIdReady(int transaction_id, String slaveid) {
		transIdReadyLock.lock();
		try {
			if (transIdReady.get(transaction_id) == null)
			{
				transIdReady.put(transaction_id, new ArrayList<String>());
			}
			if (!transIdReady.get(transaction_id).contains(slaveid))
			{
				transIdReady.get(transaction_id).add(slaveid);
			}
		} finally {
			transIdReadyLock.unlock();
		}
	}
	// end track the slaves ready
	
	// Track the slave ids that have commited a given transaction id
	private void setTransIdCommited(int transaction_id, String slaveid) {
		transIdCommitedLock.lock();
		try {
			if (transIdCommited.get(transaction_id) == null)
			{
				transIdCommited.put(transaction_id, new ArrayList<String>());
			}
			if (!transIdCommited.get(transaction_id).contains(slaveid))
			{
				transIdCommited.get(transaction_id).add(slaveid);
			}
		} finally {
			transIdCommitedLock.unlock();
		}
	}
	// end track the slaves commit
	
	// Announce retry	
	
	private void setCheckTineToRetryAnnounce(boolean status) {
		checkTimeToRetryAnnounceLock.lock();
		try {
			checkTimeToRetryAnnounce = status;
		} finally {
			checkTimeToRetryAnnounceLock.unlock();
		}
	}

	private boolean getCheckTimeToRetryAnnounce() {
		boolean temp = false;
		checkTimeToRetryAnnounceLock.lock();
		try {
			temp = checkTimeToRetryAnnounce;
		} finally {
			checkTimeToRetryAnnounceLock.unlock();
		}
		return temp;
	}

	// end Announce retry
	
	// Commit retry
	
	private void setCheckTimeToRetryCommit(boolean status) {
		checkTimeToRetryCommitLock.lock();
		try {
			checkTimeToRetryCommit = status;
		} finally {
			checkTimeToRetryCommitLock.unlock();
		}
	}
	
	private boolean getCheckTimeToRetryCommit() {
		boolean temp = false;
		checkTimeToRetryCommitLock.lock();
		try {
			temp = checkTimeToRetryCommit;
		} finally {
			checkTimeToRetryCommitLock.unlock();
		}
		return temp;
	}
	
	// end Commit retry
	
	// Announce request storage
	/** Puts an announce request into que queue (if not repeated)
	 * @param announce
	 */
	private void putAR(AnnounceRequest announce) {
		announceOrderLock.lock();
		try {
			if (!waitingAR.containsKey(announce.getTransactionId())) { 
				waitingAROrder.add(announce.getTransactionId());
				waitingAR.put(announce.getTransactionId(), announce);
				waitingARTime.put(announce.getTransactionId(),
						System.currentTimeMillis());
			}
		} finally {
			announceOrderLock.unlock();
		}
	}
	
	/** Deletes an announce request given its connection_id from the queue
	 * @param connection_id
	 */
	private void removeAR(int transaction_id) {
		announceOrderLock.lock();
		try {
			waitingAROrder.remove(transaction_id);
			waitingAR.remove(transaction_id);
			waitingARTime.remove(transaction_id);
		} finally {
			announceOrderLock.unlock();
		}
	}
	
	/** Retrieves but not deletes an AnnounceRequest for the given
	 * transaction_id
	 * @param transaction_id
	 */
	private AnnounceRequest getAR(int transaction_id) {
		AnnounceRequest ret = null;
		announceOrderLock.lock();
		try {
			ret = waitingAR.get(transaction_id);
		} finally {
			announceOrderLock.unlock();
		}
		return ret;
	}
	
	// end Announce request storage
	
	
	// Transaction commit: for a given instance stores the transaction_ids
	// that have been already commited
	
	/** Puts a transaction_id to the already commited list
	 * @param transaction_id
	 */
	private void putIHaveAlreadyCommited(int transaction_id) {
		alreadyCommitedLock.lock();
		try {
			alreadyCommited.add(transaction_id);
		} finally {
			alreadyCommitedLock.unlock();
		}
	}
	
	/** Checks whether the current instance has commited or not the 
	 * given transaction_id
	 * @param transaction_id
	 * @return
	 */
	private boolean haveIAlreadyCommited(int transaction_id) {
		boolean ret = false;
		alreadyCommitedLock.lock();
		try {
			ret = alreadyCommited.contains(transaction_id);
		} finally {
			alreadyCommitedLock.unlock();
		}
		return ret;
	}

	// end Transaction commit
	
	/** Checks if all the instances at this given point in time are ready
	 * for a commit.
	 * @param transaction_id
	 * @return
	 */
	private boolean haveThemAllReady(int transaction_id) {
		// TODO
		List<TrackerMember> members = ipidtable.getAll();
		return false;
	}
	
	/** Checks if all the instances at this given point in time have
	 * already commited the transaction id.
	 * @param transaction_id
	 * @return
	 */
	private boolean haveThemAllCommited(int transaction_id) {
		// TODO
		List<TrackerMember> members = ipidtable.getAll();
		return false;
	}
}
