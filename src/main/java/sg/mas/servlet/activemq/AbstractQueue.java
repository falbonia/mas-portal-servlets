package sg.mas.servlet.activemq;

import java.net.URI;

import javax.jms.JMSException;

/**
 * Abstract JMS queue.
 *
 * @copyright
 * @author prc
 */
public abstract class AbstractQueue {

  private URI _brokerURL;
  private String _queueName;

  public AbstractQueue(final URI brokerURL, final String queueName) {
    _brokerURL = brokerURL;
    _queueName = queueName;
  }

  public String getQueueName() {
    return _queueName;
  }

  public URI getUrl() {
    return _brokerURL;
  }

  public abstract void destroy() throws JMSException;
}
