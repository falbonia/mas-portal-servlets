package sg.mas.servlet.validation.reader;


import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;

import sg.mas.servlet.validation.messages.ValidationMessageCollection;

public interface ValidationMessageXMLReader {

  ValidationMessageCollection fromXML(Source source) throws ValidationMessageWriterException;

  ValidationMessageCollection fromXML(XMLStreamReader streamReader) throws XMLStreamException, ValidationMessageWriterException;

}

