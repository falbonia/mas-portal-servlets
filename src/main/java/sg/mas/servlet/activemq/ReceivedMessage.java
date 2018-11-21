package sg.mas.servlet.activemq;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;

import com.google.common.collect.Maps;

/**
 * Object model for JMS messages received by the queue.
 *
 * @copyright
 * @author prc
 */
public class ReceivedMessage {

  private TemporaryFile _file;

  private Map<String, Object> _headers;

  private Integer _queueId;

  private Integer _messageId;

  private ReceivedMessage(final Integer queueId, final Integer messageId, final Map<String, Object> headers, final TemporaryFile file) {
    _queueId = queueId;
    _messageId = messageId;
    _headers = headers;
    _file = file;
  }

  public static ReceivedMessage build(final Integer queueId, final Integer messageId, final Message message, final TemporaryFile file) throws JMSException {
    Map<String, Object> properties = Maps.newLinkedHashMap();
    @SuppressWarnings("unchecked")
    Enumeration<String> names = message.getPropertyNames();
    while (names.hasMoreElements()) {
      String key = names.nextElement();
      properties.put(key, message.getObjectProperty(key));
    }
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    properties.put("dateReceived", simpleDateFormat.format(new Date()));
    properties.put("__JMSMessageID", message.getJMSMessageID());
    properties.put("__JMSTimestamp", message.getJMSTimestamp());
    properties.put("__JMSPriority", message.getJMSPriority());
    properties.put("__JMSType", message.getJMSType());
    properties.put("__JMSExpiration", message.getJMSExpiration());
    return new ReceivedMessage(queueId, messageId, properties, file);
  }

  public TemporaryFile getFile() {
    return _file;
  }

  public Map<String, Object> getHeaders() {
    return _headers;
  }

  public Integer getQueueId() {
    return _queueId;
  }

  public Integer getMessageId() {
    return _messageId;
  }
}
