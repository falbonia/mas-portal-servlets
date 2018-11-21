package sg.mas.servlet.activemq;

import java.net.URI;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * Contains an ActiveMQ producer which sends messages to a named queue with method {@link OutgoingQueue#sendBytesMessageMessage(BytesMessage)}.
 *
 * @copyright
 * @author prc
 */
public class OutgoingQueue extends AbstractQueue {

  private final Connection _connection;

  private final Session _session;

  private final MessageProducer _producer;

  public OutgoingQueue(final URI brokerURL, final String queueName) throws JMSException {
    super(brokerURL, queueName);
    final ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(getUrl());
    _connection = factory.createQueueConnection();
    _connection.start();
    _session = _connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    // Change to the queue you want to use in the QA environment.
    final Destination destination = _session.createQueue(getQueueName());
    _producer = _session.createProducer(destination);
  }

  /**
   * Returns a JMS {@link BytesMessage} for configuration before sending to the queue.
   */
  public BytesMessage createNewBytesMessage() throws JMSException {
    return _session.createBytesMessage();
  }

  /**
   * Sends a JMS {@link BytesMessage} to the queue. A new {@link BytesMessage} can be obtained from {@link OutgoingQueue#createNewBytesMessage()}.
   */
  public void sendBytesMessageMessage(final BytesMessage message) throws JMSException {
    _producer.send(message);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((getUrl() == null) ? 0 : getUrl().hashCode());
    result = prime * result + ((getQueueName() == null) ? 0 : getQueueName().hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    OutgoingQueue other = (OutgoingQueue) obj;
    if (getUrl() == null) {
      if (other.getUrl() != null) {
        return false;
      }
    }
    else if (!getUrl().equals(other.getUrl())) {
      return false;
    }
    if (getQueueName() == null) {
      if (other.getQueueName() != null) {
        return false;
      }
    }
    else if (!getQueueName().equals(other.getQueueName())) {
      return false;
    }
    return true;
  }

  @Override
  public void destroy() throws JMSException {
    _producer.close();
    _session.close();
    _connection.close();
  }
}
