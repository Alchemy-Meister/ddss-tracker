package tracker.networking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;

import bitTorrent.tracker.protocol.udp.messages.custom.CustomMessage;
import bitTorrent.tracker.protocol.udp.messages.custom.peer.AnnounceRequest;
import common.utils.Utilities;
import sun.security.provider.SecureRandom;
import tracker.Const;
import tracker.subsys.TrackerSubsystem;

public class Dispatcher implements Publisher, MessageListener, Runnable {

	private static Dispatcher instance = null;
	private HashMap<Topic, List<TrackerSubsystem>> subscribers;
	private HashMap<Topic, Thread> topicThread;
	private HashMap<Topic, Boolean> topicRun;
	
	private Dispatcher() {
		this.subscribers = new HashMap<Topic, List<TrackerSubsystem>>();
		this.topicThread = new HashMap<Topic, Thread>();
		this.topicRun = new HashMap<Topic, Boolean>();
	}
	
	public static Dispatcher getInstance() {
		if (instance == null)
			instance = new Dispatcher();
		return instance;
	}

	@Override
	public void run() {
		Topic[] arrayTopic = new Topic[] { Topic.KA, Topic.ME, Topic.HI,
									Topic.DS_READY, Topic.DS_COMMIT,
									Topic.DS_DONE, Topic.ANNOUNCE_R};
		for (final Topic t : arrayTopic) {
			topicRun.put(t, true);
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					if (Const.PRINTF_DISPA) {
						System.out.println(" [DISPA] Creating subscriber,"
								+ " Topic: " + t.toString());
					}
					TopicConnection tconn = null;
					TopicSession tsession = null;
					TopicSubscriber tsubscriber = null;
					try {
						Context ctx = new InitialContext();
						TopicConnectionFactory tcf =
								(TopicConnectionFactory) ctx.lookup(
										Const.CONN_FACT_NAME);
						javax.jms.Topic topic = (javax.jms.Topic) ctx.lookup(
								getTopicName(t));
						tconn = tcf.createTopicConnection();
						byte[] bytes = new byte[20];
						new SecureRandom().engineNextBytes(bytes);
						tconn.setClientID(bytes.toString());
						tsession = tconn.createTopicSession(false,
								Session.AUTO_ACKNOWLEDGE);
						tsubscriber = tsession.createSubscriber(topic);
						tsubscriber.setMessageListener(Dispatcher.this);
						tconn.start();
						while (Dispatcher.this.topicRun.get(t)) {
							try {
							Thread.sleep(100);
							} catch(Exception e) {}
						}
					} catch (Exception e) {
						e.printStackTrace(System.err);
					}
				}
			});
			topicThread.put(t, thread);
			topicThread.get(t).start();
		}
	}
	
	public void stopAll() {
		for (Topic t : topicRun.keySet()) {
			topicRun.put(t, false);
			topicThread.get(t).interrupt();
			topicThread.put(t, null);
		}
	}
	
	/** Requests a subscrition to the given topic by the given susbsystem.
	 * @param topic
	 * @param subsystem
	 */
	public void subscribe(Topic topic, TrackerSubsystem subsystem) {
		if (subscribers.get(topic) == null)
			subscribers.put(topic, new ArrayList<TrackerSubsystem>());
		subscribers.get(topic).add(subsystem);
	}
	
	private String getTopicName(Topic topic) {
		if (topic == Topic.ANNOUNCE_R)
			return "jndi.ssdd.ANNOUNCE_R";
		else if (topic == Topic.DS_COMMIT)
			return "jndi.ssdd.DS_COMMIT";
		else if (topic == Topic.DS_DONE)
			return "jndi.ssdd.DS_DONE";
		else if (topic == Topic.DS_READY)
			return "jndi.ssdd.DS_READY";
		else if (topic == Topic.HI)
			return "jndi.ssdd.HI";
		else if (topic == Topic.KA)
			return "jndi.ssdd.KA";
		else if (topic == Topic.ME)
			return "jndi.ssdd.ME";
		else
			return null;
	}
	
	@Override
	public void publish(Topic topic, CustomMessage param) {
		String cfname = "TopicConnectionFactory";
		String topicJNDIName = getTopicName(topic);
		if (topicJNDIName != null) {
			TopicConnection topicConnection = null;
			TopicSession tsession = null;
			TopicPublisher topicPublisher = null;
			try {
				Context ctx = new InitialContext();
				TopicConnectionFactory tcf = 
						(TopicConnectionFactory) ctx.lookup(cfname);
				javax.jms.Topic myTopic = (javax.jms.Topic) ctx.lookup(
						topicJNDIName);
				topicConnection = tcf.createTopicConnection();
				tsession = topicConnection.createTopicSession(false,
						Session.AUTO_ACKNOWLEDGE);
				topicPublisher = tsession.createPublisher(myTopic);
				ObjectMessage objectMessage = tsession.createObjectMessage();
				objectMessage.setJMSType("ObjectMessage");
				objectMessage.setObject(param);
				objectMessage.setIntProperty("TOPIC", topic.getValue());
				topicPublisher.publish(objectMessage);
			} catch (Exception e) {
				e.printStackTrace(System.err);
			} finally {
				try {
					topicPublisher.close();
					tsession.close();
					topicConnection.close();
				} catch (Exception ex) {
					ex.printStackTrace(System.err);
				}
			}
		}
	}
	
	/** 
	 * Previously used by the networking thread. When a new notification enters,
	 *  the networker redirected it to the subscribers of that notification.
	 * @param topic
	 * @param param
	 */
	public void notify(Topic topic, Bundle bundle) {
		if (this.subscribers.get(topic) != null) {
			for (TrackerSubsystem subscriber : this.subscribers.get(topic))
				subscriber.receive(topic, bundle);
		}
	}

	@Override
	public void onMessage(Message mess) {
		try {
			int topic = mess.getIntProperty("TOPIC");
			Topic modelTopic = Topic.topicFromInt(topic);
			ObjectMessage objectM = (ObjectMessage) mess;
			Bundle bundle = null;
			if (modelTopic == Topic.ANNOUNCE_R) {
				AnnounceRequest ar = (AnnounceRequest) objectM.getObject();
				bundle = new Bundle(new String(Utilities.unpack(
							ar.getPeerInfo().getIpAddress())),
							ar.getPeerInfo().getPort(),
							ar);
				this.notify(Topic.ANNOUNCE_R, bundle);
			} else {
				CustomMessage cm = (CustomMessage) objectM.getObject();
				bundle = new Bundle(Const.JMS_HOST, Const.JMS_PORT, cm);
				this.notify(modelTopic, bundle);
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}
}
