package pubsub.test;

import java.util.Properties;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * 发布订阅模式
 * 发送消息
 * @author sly
 *
 */
public class TopicTest {

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
			Destination dest = (Topic) ctx.lookup("HeartBeatTopic");
			session = conn.createTopicSession(false, TopicSession.AUTO_ACKNOWLEDGE);
			MessageProducer producer = session.createProducer(dest);
			int count = 0;
			while(true){
				TextMessage msg = session.createTextMessage("test");
				producer.send(msg);
				count++;
				if(count >= 20){
					msg.setText("over");
					producer.send(msg);
					break;
				}
				Thread.sleep(500);
			}
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try {
				conn.close();
				session.close();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
	}

}
