package sg.mas.servlet.validation.messages;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;


public final class Annotation {

  private final String _value;
  private final QName _name;

  private final Map<QName, String> _attributes;

  public Annotation(final QName name, final String value) {
    this(name, value, null);
  }

  public Annotation(final QName name, final String value, final Map<QName, String> attributes) {
    if (name == null) {
      throw new IllegalArgumentException("Annotation name may not be null.");
    }
    _name = name;
    _value = value;
    _attributes = validateAttributeMap(attributes);
  }

  private static Map<QName, String> validateAttributeMap(final Map<QName, String> attributes) {
    if (attributes == null || attributes.isEmpty()) {
      return Collections.emptyMap();
    }

    final Map<QName, String> mapCopy = Collections.unmodifiableMap(new LinkedHashMap<>(attributes));
    for (Entry<QName, String> attribute : mapCopy.entrySet()) {
      if (attribute.getKey() == null) {
        throw new IllegalArgumentException("Annotation attributes may not have null names.");
      }
      if (attribute.getValue() == null) {
        throw new IllegalArgumentException("Annotation attributes may not have null values.");
      }
    }
    return mapCopy;
  }

  public QName getName() {
    return _name;
  }

  public String getValue() {
    return _value;
  }

  public Map<QName, String> getAttributes() {
    return _attributes;
  }

  @Override
  public String toString() {
    return _name + "=" + _value + (_attributes.isEmpty() ? "" : (" " + _attributes));
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((_attributes == null) ? 0 : _attributes.hashCode());
    result = prime * result + ((_name == null) ? 0 : _name.hashCode());
    result = prime * result + ((_value == null) ? 0 : _value.hashCode());
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
    Annotation other = (Annotation) obj;
    if (_attributes == null) {
      if (other._attributes != null) {
        return false;
      }
    }
    else if (!_attributes.equals(other._attributes)) {
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
    if (_value == null) {
      if (other._value != null) {
        return false;
      }
    }
    else if (!_value.equals(other._value)) {
      return false;
    }
    return true;
  }



}
