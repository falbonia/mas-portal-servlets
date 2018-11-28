package sg.mas.servlet.validation.reader;

import javax.xml.stream.XMLStreamConstants;


public class ValidationMessageXMLConstants implements XMLStreamConstants {

  protected ValidationMessageXMLConstants() {
  }

  protected static final String NAMESPACE = "http://www.corefiling.com/validation/2.0";
  protected static final String CAUSES_NAMESPACE = "http://www.corefiling.com/validation/2.0/messageCauses";

  protected static final String URI_ATTR = "url";
  protected static final String NAME_ATTR = "name";
  protected static final String ERROR_CODE = "errorCode";
  protected static final String MESSAGE_DETAIL = "messageDetail";
  protected static final String COLUMN_NUMBER = "columnNumber";
  protected static final String LINE_NUMBER = "lineNumber";
  protected static final String SEVERITY = "severity";
  protected static final String VALIDATION_MESSAGES = "validationMessages";
  protected static final String SPECIFICATION_TEXT = "specificationText";
  protected static final String SPECIFICATION_SECTION = "specificationSection";
  protected static final String SPECIFICATION_NAME = "specificationName";
  protected static final String SPECIFICATION_URI = "specificationURL";
  protected static final String VALIDATION_MESSAGE = "validationMessage";
  protected static final String SPECIFICATION_REFERENCE = "specificationReference";
  protected static final String CAUSES = "messageCauses";
  protected static final String CAUSE = "messageCause";

}
