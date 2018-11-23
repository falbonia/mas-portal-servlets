package sg.mas.servlet.activemq;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Set;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.QueueConnection;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQMessageConsumer;
import org.apache.activemq.ActiveMQSession;
import org.apache.log4j.Logger;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * Contains an ActiveMQ consumer which recieves messages from a named queue.
 * Default implementation stores the file on disk and stores the received message as a {@link ReceivedMessage}.
 *
 * @copyright
 * @author prc
 */
public class IncomingQueue extends AbstractQueue implements MessageListener {

  private final TemporaryFileManager _fileManager;

  private final QueueConnection _connection;

  private final ActiveMQSession _session;

  private final ActiveMQMessageConsumer _consumer;

  private final Map<Integer, ReceivedMessage> _receivedMessages = Maps.newLinkedHashMap();

  private static final Logger LOGGER = Logger.getLogger(IncomingQueue.class);

  private Integer _count = 0;

  private Integer _id;

  public IncomingQueue(final Integer id, final URI brokerURL, final String queueName, final TemporaryFileManager fileManager) throws JMSException {
    super(brokerURL, queueName);
    _id = id;
    _fileManager = fileManager;
    _connection = new ActiveMQConnectionFactory(brokerURL).createQueueConnection();
    _connection.start();
    _session = (ActiveMQSession) _connection.createSession(true, Session.SESSION_TRANSACTED);

    final Destination destination = _session.createQueue(queueName);
    _consumer = (ActiveMQMessageConsumer) _session.createConsumer(destination);
    LOGGER.info("Ready to consume data from " + brokerURL + " on queue " + queueName);
    _consumer.setMessageListener(this);
  }

  /**
   * Message listener handler.
   *
   * This default implementation stores the message on disk using the temporary file manager.
   */
  @Override
  public void onMessage(final Message message) {
    try {
      LOGGER.info("Message received: " + message.getJMSMessageID());
      System.out.println("Message received: " + message.getJMSMessageID());
      if (message instanceof BytesMessage) {
        final BytesMessage byteMessage = (BytesMessage) message;
        final int bodyLength = (int) byteMessage.getBodyLength();
        final byte[] byteArray = new byte[bodyLength];
        byteMessage.readBytes(byteArray);
        try {
          final TemporaryFile temporaryFile = _fileManager.storeFile(byteArray);
          Integer count = null;
          synchronized (_count) {
            count = ++_count;
          }
          synchronized (_receivedMessages) {
            _receivedMessages.put(count, ReceivedMessage.build(_id, count, message, temporaryFile));
          }
          _session.commit();
        }
        catch (IOException e) {
          LOGGER.error("An IOException occured", e);
          _session.rollback();
        }
      }
      else {
        LOGGER.info("Current ignoring non BytesMessage JMS messages");
      }
    }
    catch (JMSException e) {
      LOGGER.error("JMS Exception occured, rolling back", e);
      try {
        _session.rollback();
      }
      catch (final JMSException e1) {
        LOGGER.error("JMS Exeption occured during roll back", e1);
      }
      e.printStackTrace();
    }
  }

  public Set<Integer> getReceivedMessageIds() {
    synchronized (_receivedMessages) {
      return ImmutableSet.copyOf(_receivedMessages.keySet());
    }
  }
  public Set<ReceivedMessage> getReceivedMessages() {
    synchronized (_receivedMessages) {
      return ImmutableSet.copyOf(_receivedMessages.values());
    }
  }

  public ReceivedMessage getReceivedMessage(final int messageId) {
    synchronized (_receivedMessages) {
      return _receivedMessages.get(messageId);
    }
  }

  public boolean hasReceivedMessage(final int messageId) {
    synchronized (_receivedMessages) {
      return _receivedMessages.containsKey(messageId);
    }
  }

  public void deleteReceivedMessage(final int messageId) {
    synchronized (_receivedMessages) {
      if (_receivedMessages.containsKey(messageId)) {
        final ReceivedMessage receivedMessage = _receivedMessages.get(messageId);
        receivedMessage.getFile().getFile().delete();
        _receivedMessages.remove(messageId);
      }
    }
  }

  @Override
  public void destroy() throws JMSException {
    _consumer.close();
    _session.close();
    _connection.close();

    for (ReceivedMessage file : _receivedMessages.values()) {
      file.getFile().getFile().delete();
    }
  }

}
