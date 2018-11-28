package sg.mas.servlet.validation.messages;

import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ValidationMessageCollection extends AbstractValidationIssueCollection {

  public ValidationMessageCollection() {
  }

  public ValidationMessageCollection(final Collection<? extends ValidationMessage> validationMessages) {
    addAll(validationMessages);
  }

  public ValidationMessageCollection(final ValidationMessage... messages) {
    addAll(asList(messages));
  }

  private ValidationMessageCollection(final List<ValidationMessage> unmodifiableMessages) {
    super(Collections.unmodifiableList(unmodifiableMessages));
  }

  public ValidationMessageCollection unmodifiableCollection() {
    return new ValidationMessageCollection(getIssues());
  }

}
