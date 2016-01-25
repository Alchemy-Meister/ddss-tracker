package tracker.subsys.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import bitTorrent.tracker.protocol.udp.messages.PeerInfo;
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
	// lock for the above variables
	private final Lock announceOrderLock = new ReentrantLock();
	
	// when the anno. r. was received
	private Map<Integer, Long> transIDARSentTime;
	
	// when the commit for a transaction id was ordered
	private final Lock transIDCommitSentTimeLock = new ReentrantLock();
	private Map<Integer, Long> transIDCommitSentTime;
	
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
		this.transIDARSentTime = new HashMap<Integer, Long>();
		this.transIDCommitSentTime = new HashMap<Integer, Long>();
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
		if (Const.PRINTF_DBFTS)
			System.out.println(" [DB-FTS] Up and running.");
		while(true) {
			if (canIParticipate()) {
				Integer first_transaction = getFirstTransaction();
				if (first_transaction != null) {
					if (getCheckTimeToRetryAnnounce()) {
						Long announceSent = getARSendTime(first_transaction);
						if (announceSent != null) {
							// check if the waiting time has exceeded
							// (just master)
							if (ipidtable.amIMaster())
							{
								if (System.currentTimeMillis() - announceSent
										> Const.ANNOUNCE_RETRY)
								{
									AnnounceRequest ar = getAR(first_transaction);
									networker.notify(Topic.ANNOUNCE_R,
									// impossible to indent properly U_U
									 new Bundle(new String(Utilities.unpack(
										ar.getPeerInfo().getIpAddress())),
													ar.getPeerInfo().getPort(),
													ar));
								}
							}
							if (haveThemAllReady(first_transaction)) {
								setCheckTimeToRetryAnnounce(false);
							}
						}
					} 
					if (getCheckTimeToRetryCommit()) {
						Long commitSent = getTransIdCommitSentTime(first_transaction);
						if (commitSent != null) {
							// check if the waiting time has exceeded
							// (just master)
							if (ipidtable.amIMaster())
							{
								if (System.currentTimeMillis() - commitSent
										> Const.DS_COMMIT_RETRY)
								{
									AnnounceRequest ar = getAR(first_transaction);
									if (ar != null) {
										DSCommitM commit = new DSCommitM(
												ar.getConnectionId(),
												ar.getAction().value(),
												first_transaction);
										putTransIdCommitSendTime(first_transaction);
										networker.publish(Topic.DS_COMMIT,
												commit);
									}
								}
							}
							// check again if they have all commited
							if (haveThemAllCommited(first_transaction)) {
								setCheckTimeToRetryCommit(false);
								cleanTransactionId(first_transaction);
							}
						}
					}
				}
			}
			
			try { Thread.sleep(1100);} catch (Exception e) {}
		} 
	}
	
	private boolean canIParticipate() {
		LongLong l = ipidtable.getMyId();
		if (l != null && !l.toString().equals(Const.UNASIGNED_ID.toString()))
			return true;
		return false;
	}
	
	/**
	 * Returns the first transaction that we have to check.
	 * @return
	 */
	private Integer getFirstTransaction() {
		Integer ret = null;
		announceOrderLock.lock();
		try {
			if (waitingAROrder.size() > 0)
				ret = waitingAROrder.get(0);
		} finally {
			announceOrderLock.unlock();
		}
		return ret;
	}
	
	/** When a transaction is completely processed, delete it.
	 * @param transaction_id
	 */
	private synchronized void cleanTransactionId(int transaction_id) {
		// announces
		removeAR(transaction_id);
		// readys
		removeReady(transaction_id);
		// commits
		removeCommit(transaction_id);
	}

	private void insertContents(String sha, int address, int port) {
		manager.connect();
		try {
			manager.insertContents(sha,	PeerInfo.toStringIpAddress(address),
					port);
		} catch (SQLException e) {
			e.printStackTrace(System.err);
		}
		manager.disconnect();
	}
	
	@Override
	public void receive(Topic topic, Bundle bundle) {
		if (canIParticipate()) {
			if (Const.PRINTF_DBFTS)
				System.out.println();
			if (topic == Topic.ANNOUNCE_R) {
				AnnounceRequest announce = bundle.getPeerMessage();
				if (Const.PRINTF_DBFTS) {
					System.out.println(" [DB-FTS] Announce received, transac: " +
							announce.getTransactionId() + ", sha1: "
							+ announce.getInfoHash());
				}
				// If I am master without peers, I don't have to handle
				// this transaction_id
				if (ipidtable.amIMaster() && ipidtable.getCountMembers() > 1) {
					// initialise the list of peers ready for this tran id
					transIdReadyLock.lock();
					try {
						if (!transIdReady.containsKey(announce.getTransactionId())) {
							transIdReady.put(announce.getTransactionId(),
									new ArrayList<String>());
						}
					} finally {
						transIdReadyLock.unlock();
					}
					putAR(announce);
				} else {
					if (Const.PRINTF_DBFTS) {
						System.out.println(" [DB-FTS] Master alone, auto-saving " 
								 + " announce data.");
					}
					insertContents(announce.getInfoHash(),
							announce.getPeerInfo().getIpAddress(),
							announce.getPeerInfo().getPort());				
				}
				if (!ipidtable.amIMaster()) {
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
							if (Const.PRINTF_DBFTS) {
								System.out.println(" [DB-FTS] DS-Ready sent,"
										+ " trans_id: " +
										announce.getTransactionId());
							}
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
				if (Const.PRINTF_DBFTS) {
					System.out.println(" [DB-FTS] DS-Ready received, trans_id: " +
							dsready.getTransactionId());
				}
				// update the list of peers ready for the given trans id
				String id = dsready.getId().toString();
				setTransIdReady(dsready.getTransactionId(), id);
				if (ipidtable.amIMaster()) {
					// after updating check if we have them all to send 
					// a DS_COMMIT for the first transaction id
					Integer first_transaction = getFirstTransaction();
					if (Const.PRINTF_DBFTS) {
						System.out.println(" [DB-FTS] Checking ready trans: "
								+ first_transaction);
					}
					if (first_transaction != null) {
						if (haveThemAllReady(first_transaction)) {
							if (Const.PRINTF_DBFTS) {
								System.out.println(" [DB-FTS] All peers ready"
										+ " for trans_id: " +
										first_transaction);
							}
							setCheckTimeToRetryAnnounce(false);
							AnnounceRequest ar = getAR(first_transaction);
							if (ar != null) {
								DSCommitM commit = new DSCommitM(
										ar.getConnectionId(),
										ar.getAction().value(),
										first_transaction);
								putTransIdCommitSendTime(first_transaction);
								networker.publish(Topic.DS_COMMIT, commit);
								if (Const.PRINTF_DBFTS) {
									System.out.println(" [DB-FTS] DS-Commit "
											+ "sent, trans_id: " +
											commit.getTransactionId());
								}
							}
						} else {
							if (Const.PRINTF_DBFTS) {
								System.out.println(" [DB-FTS] Not ready "
										+ "for trans: " + first_transaction);
							}
							// The other thread checks if we have to send the
							// announce again or not
							setCheckTimeToRetryAnnounce(true);
						}
					}
				}
			} else if (topic == Topic.DS_COMMIT) {
				// Master and slaves commit at the same time
				DSCommitM commit = (DSCommitM) bundle.getMessage();
				AnnounceRequest an = getAR(commit.getTransactionId());
				if (Const.PRINTF_DBFTS) {
					System.out.println(" [DB-FTS] DS-Commit received, trans_id: " +
							commit.getTransactionId());
				}
				// Check whether we have commited such transaction id yet
				if (!haveIAlreadyCommited(commit.getTransactionId())) {
					if (an.getInfoHash() != null && an.getPeerInfo() != null) {
							insertContents(an.getInfoHash(),
									an.getPeerInfo().getIpAddress(),
									an.getPeerInfo().getPort());
							putIHaveAlreadyCommited(commit.getTransactionId());
					}
					if (Const.PRINTF_DBFTS) {
						System.out.println(" [DB-FTS] Commited trans_id: " +
								commit.getTransactionId());
					}
				} else {
					if (Const.PRINTF_DBFTS) {
						System.out.println(" [DB-FTS] Not commiting trans_id: " +
								commit.getTransactionId());
					}
				}
				// either way, send that the transaction is done
				// (if we are not master)
				if (!ipidtable.amIMaster()) {
					LongLong myid = ipidtable.getMyId();
					if (myid != null) {
						networker.publish(Topic.DS_DONE, 	
						new DSDoneM(commit.getTransactionId(), myid));
						if (Const.PRINTF_DBFTS) {
							System.out.println(" [DB-FTS] DS-done sent, trans_id: " +
									commit.getTransactionId());
						}
					}
				}
			} else if (topic == Topic.DS_DONE) {
				DSDoneM done = (DSDoneM) bundle.getMessage();
				setTransIdCommited((int) done.getConnection_id(),
						done.getSenderId().toString());
				if (Const.PRINTF_DBFTS) {
					System.out.println(" [DB-FTS] DS-done received, trans_id: " +
							done.getConnection_id());
				}
				if (ipidtable.amIMaster()) {
					// check if all have commited the first transaction
					Integer first_transaction = getFirstTransaction();
					if (Const.PRINTF_DBFTS) {
						System.out.println(" [DB-FTS] Checking commit trans: "
								+ first_transaction);
					}
					if (first_transaction != null) {
						if (haveThemAllCommited(first_transaction)) {
							setCheckTimeToRetryCommit(false);
							cleanTransactionId(first_transaction);
							if (Const.PRINTF_DBFTS) {
								System.out.println(" [DB-FTS] Trans_id: " +
										first_transaction + " done!");
							}
						} else {
							if (Const.PRINTF_DBFTS) {
								System.out.println(" [DB-FTS] Not ready "
										+ "for trans: " + first_transaction);
							}
							setCheckTimeToRetryCommit(true);
						}
					}
				}
			}
		}
	}
	
	private void removeCommit(int transaction_id) {
		transIdCommitedLock.lock();
		try {
			transIdCommited.remove(transaction_id);
		} finally {
			transIdCommitedLock.unlock();
		}
		transIDCommitSentTimeLock.lock();
		try {
			transIDCommitSentTime.remove(transaction_id);
		} finally {
			transIdCommitedLock.unlock();
		}
		alreadyCommitedLock.lock();
		try {
			alreadyCommited.remove(transaction_id);
		} finally {
			alreadyCommitedLock.unlock();
		}
	}
	
	private void putTransIdCommitSendTime(int transactionId) {
		transIDCommitSentTimeLock.lock();
		try {
			if (transIDCommitSentTime.get(transactionId) == null)
				transIDCommitSentTime.put(transactionId,
						System.currentTimeMillis());
		} finally {
			transIDCommitSentTimeLock.unlock();
		}
	}
	
	private Long getTransIdCommitSentTime(int transactionId) {
		Long ret = null;
		transIDCommitSentTimeLock.lock();
		try {
			ret = transIDCommitSentTime.get(transactionId);
		} finally {
			transIDCommitSentTimeLock.unlock();
		}
		return ret;
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
			if (Const.PRINTF_DBFTS) {
				System.out.println(" [DB-DTS] Ready for trans ("
						+ transaction_id + ") : "
						+ transIdReady.get(transaction_id).toString());
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
	
	private void setCheckTimeToRetryAnnounce(boolean status) {
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
				transIDARSentTime.put(announce.getTransactionId(),
						System.currentTimeMillis());
			}
			if (Const.PRINTF_DBFTS) {
				System.out.println(" [DB-FTS] trans in queue: "
						+ waitingAROrder.toString());
			}
		} finally {
			announceOrderLock.unlock();
		}
	}
	
	private Long getARSendTime(int transactionId) {
		Long ret = null;
		announceOrderLock.lock();
		try {
			ret = transIDARSentTime.get(transactionId);
		} finally {
			announceOrderLock.unlock();
		}
		return ret;
	}
	
	/** Deletes an announce request given its connection_id from the queue
	 * @param connection_id
	 */
	private void removeAR(int transaction_id) {
		announceOrderLock.lock();
		try {
			waitingAROrder.remove(transaction_id);
			waitingAR.remove(transaction_id);
			transIDARSentTime.remove(transaction_id);
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
	
	/** Removes the given trans id from the ready list
	 * @param transaction_id
	 */
	private void removeReady(int transaction_id) {
		transIdReadyLock.lock();
		try {
			transIdReady.remove(transaction_id);
		} finally {
			transIdReadyLock.unlock();
		}
	}
	
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
		// We have to ignore master, since it does not send ds_ready messages
		List<TrackerMember> members = ipidtable.getAll();
		List<String> copyIdReady = null;
		transIdReadyLock.lock();
		try {
			if (transIdReady.containsKey(transaction_id))
				copyIdReady = new ArrayList<String>(transIdReady.get(
						transaction_id));
		} finally {
			transIdReadyLock.unlock();
		}
		boolean allReady = true;
		LongLong masterId = ipidtable.getMasterID();
		if (masterId != null && copyIdReady != null) {
			for (TrackerMember member : members) {
				if (!member.getId().toString().equals(masterId.toString())) {
					if (!copyIdReady.contains(member.getId().toString())) {
						if (Const.PRINTF_DBFTS) {
							System.out.println(" [DB-FTS] I miss DS_READY"
									+ " trans: " + transaction_id + ", from:"
									+ member.getId().toString());
						}
						allReady = false;
						break;
					}
				}
			}
		} else
			allReady = false;
		return allReady;
	}
	
	/** Checks if all the instances at this given point in time have
	 * already commited the transaction id.
	 * @param transaction_id
	 * @return
	 */
	private boolean haveThemAllCommited(int transaction_id) {
		// We have to ignore master, since it does not send ds_done messages
		List<TrackerMember> members = ipidtable.getAll();
		List<String> copyIdCommit = null;
		transIdCommitedLock.lock();
		try {
			if (transIdCommited.containsKey(transaction_id))
				copyIdCommit = new ArrayList<String>(transIdCommited.get(
						transaction_id));
		} finally {
			transIdCommitedLock.unlock();
		}
		boolean allcommit = true;
		LongLong masterId = ipidtable.getMasterID();
		if (masterId != null && copyIdCommit != null) {
			for (TrackerMember member : members) {
				if (!member.getId().toString().equals(masterId.toString())) {
					if (!copyIdCommit.contains(member.getId().toString())) {
						if (Const.PRINTF_DBFTS) {
							System.out.println(" [DB-FTS] I miss DS_DONE"
									+ " trans: " + transaction_id + ", from:"
									+ member.getId().toString());
						}
						allcommit = false;
						break;
					}
				}
			}
		} else
			allcommit = false;
		return allcommit;
	}
}
