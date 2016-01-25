package tracker.subsys.election;

import tracker.subsys.TrackerSubsystem;
import tracker.subsys.cfts.IpIdTable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import bitTorrent.tracker.protocol.udp.messages.custom.LongLong;
import bitTorrent.tracker.protocol.udp.messages.custom.me.MasterElectionM;
import tracker.Const;
import tracker.db.model.TrackerMember;
import tracker.networking.Bundle;
import tracker.networking.Topic;

/** In charge of handling the master election system.
 * @author Irene
 * @author Jesus
 */
public class MasterElectionSys extends TrackerSubsystem implements  Runnable {
	
	private static MasterElectionSys instance = null;
	private IpIdTable ipidTable = null;
	private Boolean inProgress = false;
	private Map<String, String> meAuthorVote = null;
	private Timer timerME = null;
	
	private MasterElectionSys() {
		super();
		ipidTable = IpIdTable.getInstance();
		meAuthorVote = new HashMap<String, String>();
		timerME = new Timer();
	}
	
	public static MasterElectionSys getInstance() {
		if (instance == null)
			instance = new MasterElectionSys();
		return instance;
	}

	@Override
	public void run() {
		TrackerSubsystem.networker.subscribe(Topic.ME, this);
	}
	
	public void stop() {
		timerME.cancel();
	}
	
	private boolean canIVote() {
		LongLong myid = ipidTable.getMyId();
		if (myid == null ||
				myid.toString().equals(Const.UNASIGNED_ID.toString())) 
			return false;
		return true;
	}
	/**
	 * Starts a new master election process.
	 */
	public void startMasterElection() {
		if (canIVote()) {
			TrackerMember myVote = ipidTable.getMemberLowestId();
			if (myVote != null) {
				meAuthorVote.put(ipidTable.getMyId().toString(),
						myVote.getId().toString());
				if (Const.PRINTF_ME) {
					System.out.println(" [ME] New Master Election started!");
					System.out.println(" [ME] I have voted to: " + myVote.getId());
				}
				inProgress = true;
				timerME.schedule(new METimerTask(ipidTable.getMyId(), myVote.getId()),
						0);
			}
			boolean checkFallen = true;
			while(inProgress) {
				if (allVotesReceived()) {
					if (Const.PRINTF_ME) {
						System.out.println(" [ME] All the votes received");
						System.out.println(" [ME] votes: " + meAuthorVote.toString());
					}
					Map<String, Integer> idVotes = new HashMap<>();
					for (String author : meAuthorVote.keySet()) {
						if (idVotes.get(author) == null)
								idVotes.put(author, 0);
						idVotes.put(author, idVotes.get(author) + 1);
					}
					boolean broken = false;
					String author = null;
					int maxVotes = 0;
					for (String id : idVotes.keySet()) {
						if (idVotes.get(id) > maxVotes) {
							author = id;
							maxVotes = idVotes.get(id);
						} else if (idVotes.get(id) == maxVotes)
							broken = true;
					}
					if (Const.PRINTF_ME) {
						System.out.println(" [ME] Results: " + idVotes.toString());
					}
					if (!broken && author != null) {
						if (Const.PRINTF_ME) {
							System.out.println(" [ME] The winner is: "
									+ author);
						}
						ipidTable.electMaster(new LongLong(author));
						inProgress = false;
						checkFallen = false;
						timerME.cancel();
						timerME = new Timer();
						ipidTable.updateMaster();
					} else {
						if (Const.PRINTF_ME) {
							System.out.println(" [ME] Consensus unreached.");
						}
						
					}
					// if there is an error the me-process will continue
				}
				try { 
					Thread.sleep(3000);
				} catch(Exception e) {
					e.printStackTrace(System.err);
				}
				if (checkFallen)
					ipidTable.checkFallenMembers();
			}
		}
	}
	
	private boolean allVotesReceived() {
		List<TrackerMember> members = ipidTable.getAll();
		for (TrackerMember m : members) {
			if (meAuthorVote.get(m.getId().toString()) == null) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void receive(Topic topic, Bundle bundle) {
		if (inProgress) {
			if (topic == Topic.ME) {
				MasterElectionM mem = (MasterElectionM) bundle.getMessage();
				if (!ipidTable.getMyId().equals(mem.getAuthor().toString())) {
					meAuthorVote.put(mem.getAuthor().toString(),
							mem.getPayload().toString());
					if (Const.PRINTF_ME) {
						System.out.println(" [ME] Vote to: " + mem.getPayload()
						+ ", from: " + mem.getAuthor());
					}
				}
			}
		}
		
	}
	
	public boolean isInProgress() {
		boolean ret = false;
		synchronized (inProgress) {
			ret = inProgress;
		}
		return ret;
	}
	
	private class METimerTask extends TimerTask {
		
		private LongLong author, vote;
		
		public METimerTask(LongLong author, LongLong vote) {
			this.author = author;
			this.vote = vote;
		}
		@Override
		public void run() {
			networker.publish(Topic.ME, new MasterElectionM(vote, author));
			timerME = new Timer();
			timerME.schedule(new METimerTask(author, vote), Const.ME_EVERY);
		}
	}
}
