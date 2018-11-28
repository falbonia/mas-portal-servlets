package sg.mas.servlet.validation.messages;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class AbstractValidationIssueCollection extends AbstractList<ValidationMessage> {

  private final List<ValidationMessage> _messages;

  public AbstractValidationIssueCollection() {
    this(new ArrayList<ValidationMessage>());
  }

  public AbstractValidationIssueCollection(final List<ValidationMessage> messages) {
    _messages = messages;
  }

  @Override
  public ValidationMessage get(final int i) {
    return _messages.get(i);
  }

  @Override
  public ValidationMessage set(final int i, final ValidationMessage message) {
    final ValidationMessage old = _messages.get(i);
    _messages.set(i, message);
    return old;
  }

  @Override
  public void add(final int i, final ValidationMessage o) {
    _messages.add(i, o);
  }

  @Override
  public boolean add(final ValidationMessage m) {
    add(size(), m);
    return true;
  }

  @Override
  public ValidationMessage remove(final int i) {
    return _messages.remove(i);
  }

  @Override
  public boolean remove(final Object o) {
    return _messages.remove(o);
  }

  @Override
  public int size() {
    return _messages.size();
  }

  public Severity getSeverity() {
    Severity result = Severity.OK; // default
    for (ValidationMessage message : this) {
      if (message.getSeverity().compareTo(result) > 0) {
        result = message.getSeverity();
      }
    }
    return result;
  }

  protected List<ValidationMessage> getIssues() {
    return _messages;
  }

}
