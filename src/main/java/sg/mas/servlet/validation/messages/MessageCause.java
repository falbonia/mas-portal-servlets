package sg.mas.servlet.validation.messages;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class MessageCause {

  private int _lineNumber = -1;

  private int _columnNumber = -1;

  private URI _location = null;

  private List<Annotation> _annotations = new ArrayList<>();

  private String _name = null;

  private Set<MessageCause> _causes = new LinkedHashSet<>();

  public MessageCause() {
  }

  public void setName(final String name) {
    _name = name;
  }

  public String getName() {
    return _name;
  }

  public void setColumnNumber(final int columnNumber) {
    _columnNumber = columnNumber;
  }

  public void setLineNumber(final int lineNumber) {
    _lineNumber = lineNumber;
  }

  public void setLocation(final URI location) {
    _location = location;
  }

  public void setAnnotations(final List<Annotation> annotations) {
    _annotations = new ArrayList<>(annotations);
  }

  public void setCauses(final Set<MessageCause> causes) {
    _causes = new LinkedHashSet<>(causes);
  }

  public int getLineNumber() {
    return _lineNumber;
  }

  public int getColumnNumber() {
    return _columnNumber;
  }

  public URI getLocation() {
    return _location;
  }

  public Set<MessageCause> getCauses() {
    return _causes;
  }

  public List<Annotation> getAnnotations() {
    return _annotations;
  }

  @Override
  public String toString() {
    if (_location == null && _name == null) {
      return "Cause with no name";
    }
    return
      (_name == null ? "" : _name)
      + (_location != null && _name != null ? " " : "")
      + (_location == null ? "" : String.format("%s (line %d, column %d)", _location, _lineNumber, _columnNumber))
      + (_annotations.isEmpty() ? "" : " with annotations " + _annotations.toString());
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((_annotations == null) ? 0 : _annotations.hashCode());
    result = prime * result + ((_causes == null) ? 0 : _causes.hashCode());
    result = prime * result + _columnNumber;
    result = prime * result + _lineNumber;
    result = prime * result + ((_location == null) ? 0 : _location.hashCode());
    result = prime * result + ((_name == null) ? 0 : _name.hashCode());
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
    MessageCause other = (MessageCause) obj;
    if (_annotations == null) {
      if (other._annotations != null) {
        return false;
      }
    }
    else if (!_annotations.equals(other._annotations)) {
      return false;
    }
    if (_causes == null) {
      if (other._causes != null) {
        return false;
      }
    }
    else if (!_causes.equals(other._causes)) {
      return false;
    }
    if (_columnNumber != other._columnNumber) {
      return false;
    }
    if (_lineNumber != other._lineNumber) {
      return false;
    }
    if (_location == null) {
      if (other._location != null) {
        return false;
      }
    }
    else if (!_location.equals(other._location)) {
      return false;
    }
    if (_name == null) {
      if (other._name != null) {
        return false;
      }
    }
    else if (!_name.equals(other._name)) {
      return false;
    }
    return true;
  }
}
