package sg.mas.servlet.validation.reader;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import sg.mas.servlet.validation.messages.ValidationMessageCollection;

public class EnvelopedValidationMessageXMLReader extends AbstractValidationMessageXMLReader {

  private final ValidationMessageXMLReaderImpl _delegate = new ValidationMessageXMLReaderImpl() {
    @Override
    protected void preamble(final XMLStreamReader streamReader) throws XMLStreamException {
      // The outer class consumes the start tag.
    }
  };

  @Override
  public ValidationMessageCollection fromXML(final XMLStreamReader streamReader) throws XMLStreamException, ValidationMessageWriterException {
    while (streamReader.next() != END_DOCUMENT) {
      if (streamReader.isStartElement() && VALIDATION_MESSAGES.equals(streamReader.getLocalName()) && NAMESPACE.equals(streamReader.getNamespaceURI())) {
        return _delegate.fromXML(streamReader);
      }
    }
    throw new ValidationMessageWriterException("No validation messages found.");
  }

}
