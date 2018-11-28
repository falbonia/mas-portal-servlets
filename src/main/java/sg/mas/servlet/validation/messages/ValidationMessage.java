package sg.mas.servlet.validation.messages;

import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Set;

public class ValidationMessage {

  private final String _errorCode;

  private final Severity _severity;

  private int _lineNumber = -1;

  private int _columnNumber = -1;

  private String _messageDetail = "";

  private URI _uri;

  private String _description;

  private Set<MessageCause> _causes = new LinkedHashSet<>();

  public ValidationMessage(final String errorCode, final Severity severity) {
    if (errorCode == null || severity == null) {
      throw new IllegalArgumentException("Both the error code and the severity must be non-null");
    }
    _severity = severity;
    _errorCode = errorCode;
  }

  public int getColumnNumber() {
    return _columnNumber;
  }

  public String getErrorCode() {
    return _errorCode;
  }

  public int getLineNumber() {
    return _lineNumber;
  }

  public String getMessageDetail() {
    return _messageDetail;
  }

  public Severity getSeverity() {
    return _severity;
  }

  public URI getURI() {
    return _uri;
  }

  public String getDescription() {
    return _description;
  }

  public void setURI(final URI uri) {
    _uri = uri;
  }

  public void setMessageDetail(final String messageDetail) {
    _messageDetail = messageDetail;
  }

  public void setDescription(final String description) {
    _description = description;
  }

  public void setLineNumber(final int lineNumber) {
    _lineNumber = lineNumber;
  }

  public void setColumnNumber(final int columnNumber) {
    _columnNumber = columnNumber;
  }

  public void setCauses(final Set<MessageCause> causes) {
    if (causes == null) {
      throw new IllegalArgumentException("Causes must be non-null (but may be empty).");
    }
    _causes = causes;
  }

  public Set<MessageCause> getCauses() {
    return _causes;
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
    final ValidationMessage other = (ValidationMessage) obj;
    if (_columnNumber != other._columnNumber) {
      return false;
    }
    if (_lineNumber != other._lineNumber) {
      return false;
    }
    if (_messageDetail == null) {
      if (other._messageDetail != null) {
        return false;
      }
    }
    else if (!_messageDetail.equals(other._messageDetail)) {
      return false;
    }
    if (_severity == null) {
      if (other._severity != null) {
        return false;
      }
    }
    else if (!_severity.equals(other._severity)) {
      return false;
    }
    if (_errorCode == null) {
      if (other._errorCode != null) {
        return false;
      }
    }
    else if (!_errorCode.equals(other._errorCode)) {
      return false;
    }
    if (_uri == null) {
      if (other._uri != null) {
        return false;
      }
    }
    else if (!_uri.equals(other._uri)) {
      return false;
    }
    if (_description == null) {
      if (other._description != null) {
        return false;
      }
    }
    else if (!_description.equals(other._description)) {
      return false;
    }
    if (!_causes.equals(other._causes)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + _columnNumber;
    result = prime * result + _lineNumber;
    result = prime * result + ((_messageDetail == null) ? 0 : _messageDetail.hashCode());
    result = prime * result + ((_severity == null) ? 0 : _severity.hashCode());
    result = prime * result + ((_errorCode == null) ? 0 : _errorCode.hashCode());
    result = prime * result + ((_uri == null) ? 0 : _uri.hashCode());
    result = prime * result + ((_description == null) ? 0 : _description.hashCode());
    return result;
  }

}
