package sg.mas.servlet.validation.reader;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import sg.mas.servlet.validation.messages.Annotation;
import sg.mas.servlet.validation.messages.MessageCause;
import sg.mas.servlet.validation.messages.Severity;
import sg.mas.servlet.validation.messages.ValidationMessage;
import sg.mas.servlet.validation.messages.ValidationMessageCollection;

public class ValidationMessageXMLReaderImpl extends AbstractValidationMessageXMLReader {

  @Override
  public ValidationMessageCollection fromXML(final XMLStreamReader streamReader) throws XMLStreamException, ValidationMessageWriterException {
    final ValidationMessageCollection messages = new ValidationMessageCollection();
    preamble(streamReader);
    streamReader.require(START_ELEMENT, NAMESPACE, VALIDATION_MESSAGES);
    while (streamReader.nextTag() == START_ELEMENT) {
      streamReader.require(START_ELEMENT, NAMESPACE, VALIDATION_MESSAGE);
      final ValidationMessage message = new ValidationMessage(streamReader.getAttributeValue(null, ERROR_CODE), readSeverity(streamReader));
      message.setLineNumber(readInt(streamReader, LINE_NUMBER));
      message.setColumnNumber(readInt(streamReader, COLUMN_NUMBER));
      readURI(streamReader, message);
      message.setMessageDetail(streamReader.getAttributeValue(null, MESSAGE_DETAIL));
      streamReader.nextTag();
      while (START_ELEMENT == streamReader.getEventType()) {
        if (MESSAGE_DETAIL.equals(streamReader.getLocalName())) {
          streamReader.require(START_ELEMENT, NAMESPACE, MESSAGE_DETAIL);
          message.setMessageDetail(streamReader.getElementText());
        }
        if (SPECIFICATION_REFERENCE.equals(streamReader.getLocalName())) {
          streamReader.require(START_ELEMENT, NAMESPACE, SPECIFICATION_REFERENCE);
          ignoreSpecReference(streamReader);
        }
        if (CAUSES.equals(streamReader.getLocalName())) {
          streamReader.require(START_ELEMENT, CAUSES_NAMESPACE, CAUSES);
          final Set<MessageCause> causes = new LinkedHashSet<>();
          streamReader.nextTag();
          while (START_ELEMENT == streamReader.getEventType()) {
            if (CAUSE.equals(streamReader.getLocalName())) {
              causes.add(readCause(streamReader));
            }
            streamReader.nextTag();
          }
          message.setCauses(causes);
        }
        streamReader.nextTag();
      }
      if (streamReader.getEventType() == START_ELEMENT) {
        // A nested spec ref element.
        ignoreSpecReference(streamReader);
        streamReader.nextTag();
      }
      messages.add(message);
      streamReader.require(END_ELEMENT, NAMESPACE, VALIDATION_MESSAGE);
    }
    return messages;
  }


  protected void preamble(final XMLStreamReader streamReader) throws XMLStreamException {
    streamReader.nextTag();
  }

  private MessageCause readCause(final XMLStreamReader streamReader) throws ValidationMessageWriterException, XMLStreamException {
    streamReader.require(START_ELEMENT, CAUSES_NAMESPACE, CAUSE);
    MessageCause result = new MessageCause();
    result.setName(streamReader.getAttributeValue(null, NAME_ATTR));
    result.setLocation(readURI(streamReader.getAttributeValue(null, URI_ATTR)));
    result.setLineNumber(readInt(streamReader, LINE_NUMBER));
    result.setColumnNumber(readInt(streamReader, COLUMN_NUMBER));

    streamReader.nextTag();
    final List<Annotation> annotations = new ArrayList<>();
    while (START_ELEMENT == streamReader.getEventType()) {
      final QName annotationName = streamReader.getName();
      final Map<QName, String> attributes = new LinkedHashMap<>();
      for (int i = 0; i < streamReader.getAttributeCount(); ++i) {
        final QName attributeName = streamReader.getAttributeName(i);
        final String attributeValue = streamReader.getAttributeValue(i);
        attributes.put(attributeName, attributeValue);
      }
      // This moves to END_ELEMENT.
      final String annotationValue = streamReader.getElementText();
      annotations.add(new Annotation(annotationName, annotationValue, attributes));
      streamReader.nextTag();
    }
    result.setAnnotations(annotations);
    return result;
  }

  private void ignoreSpecReference(final XMLStreamReader streamReader) throws XMLStreamException {
    streamReader.require(START_ELEMENT, NAMESPACE, SPECIFICATION_REFERENCE);
    if (streamReader.nextTag() == START_ELEMENT) {
      streamReader.require(START_ELEMENT, NAMESPACE, SPECIFICATION_TEXT);
      streamReader.getElementText();
      streamReader.require(END_ELEMENT, NAMESPACE, SPECIFICATION_TEXT);
      streamReader.nextTag();
    }
    streamReader.require(END_ELEMENT, NAMESPACE, SPECIFICATION_REFERENCE);
  }

  private void readURI(final XMLStreamReader streamReader, final ValidationMessage message) throws ValidationMessageWriterException {
    final String uri = streamReader.getAttributeValue(null, URI_ATTR);
    message.setURI(readURI(uri));
  }

  private URI readURI(final String uri) throws ValidationMessageWriterException {
    if (uri == null) {
      return null;
    }
    try {
      return new URI(uri);
    }
    catch (final URISyntaxException e) {
      throw new ValidationMessageWriterException(e);
    }
  }

  private Integer readInt(final XMLStreamReader streamReader, final String localName) {
    return Integer.valueOf(streamReader.getAttributeValue(null, localName));
  }

  private Severity readSeverity(final XMLStreamReader streamReader) throws ValidationMessageWriterException {
    try {
      return Severity.valueOf(streamReader.getAttributeValue(null, SEVERITY));
    }
    catch (final IllegalArgumentException e) {
      throw new ValidationMessageWriterException(e);
    }
  }

}

