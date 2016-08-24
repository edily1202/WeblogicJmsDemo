package pubsub.test;

import java.util.Properties;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * 发布/订阅模型
 * 接收消息
 * @author sly
 *
 */
public class SubTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Properties props = new Properties();
		props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
		props.setProperty(Context.PROVIDER_URL, "t3://172.20.182.62:7001");
//		props.setProperty(Context.SECURITY_PRINCIPAL, "weblogic");
//		props.setProperty(Context.SECURITY_CREDENTIALS, "weblogic12");
		TopicConnection conn = null;
		TopicSession session = null;
		
		try {
			InitialContext ctx = new InitialContext(props);
			TopicConnectionFactory factory = (TopicConnectionFactory) ctx.lookup("HeartBeatConnectionFactory");
			conn = factory.createTopicConnection();
			Topic dest = (Topic) ctx.lookup("HeartBeatTopic");
			session = conn.createTopicSession(false, TopicSession.AUTO_ACKNOWLEDGE);
			TopicSubscriber sub = session.createSubscriber(dest);
			conn.start();
			while(true){
				Message msg = sub.receive();
				if(msg instanceof TextMessage){
					TextMessage tmsg = (TextMessage) msg;
					String text = tmsg.getText();
					System.out.println(text);
//					System.out.println("收到消息，我要回复");
					if("over".equals(text)){
						break;
					}
					
				} else if(msg instanceof MapMessage) {
					MapMessage map = (MapMessage) msg;
					String msgType = map.getString("MSG_TYPE");
					System.out.println(msgType);
					System.out.println(map.getString("user"));
					System.out.println(map.getString("sessionID"));
					System.out.println(map.getString("ipAddress"));
					
					if(msgType.equals("HEART_BEAT")) {
						MapMessage reply = session.createMapMessage();
						reply.setString("MSG_TYPE", "DO_LOCK_SCREEN");
						reply.setString("jettySessionID", map.getString("jettySessionID"));
						reply.setString("messageID", map.getString("messageID"));
						reply.setJMSCorrelationID(msg.getJMSCorrelationID());
						
						MessageProducer producer = session.createProducer(null);  
				        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);  
				        producer.send(msg.getJMSReplyTo(), reply);
					}
				}
			}
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
//			try {
//				conn.close();
//				session.close();
//			} catch (JMSException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
		
	}

}
