package sg.mas.servlet.validation.reader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;

import com.ctc.wstx.stax.WstxInputFactory;

import sg.mas.servlet.validation.messages.ValidationMessageCollection;

public abstract class AbstractValidationMessageXMLReader extends ValidationMessageXMLConstants implements ValidationMessageXMLReader {

  private final XMLInputFactory _inputFactory;

  public AbstractValidationMessageXMLReader() {
    _inputFactory = new WstxInputFactory();
  }

  @Override
  public ValidationMessageCollection fromXML(final Source source) throws ValidationMessageWriterException {
    try {
      final XMLStreamReader streamReader = _inputFactory.createXMLStreamReader(source);
      try {
        return fromXML(streamReader);
      }
      finally {
        streamReader.close();
      }
    }
    catch (XMLStreamException e) {
      throw wrap(e);
    }
  }

  ValidationMessageWriterException wrap(final Exception e) {
    return new ValidationMessageWriterException(e);
  }

}