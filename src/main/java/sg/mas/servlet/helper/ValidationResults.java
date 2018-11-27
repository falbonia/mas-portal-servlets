package sg.mas.servlet.helper;

import java.util.List;

public class ValidationResults {
	
	public ValidationResults(List<ValidationMessage> validationMessages) {
		super();
		ValidationMessages = validationMessages;
	}

	List<ValidationMessage> ValidationMessages;

	public List<ValidationMessage> getValidationMessages() {
		return ValidationMessages;
	}

	public void setValidationMessages(List<ValidationMessage> validationMessages) {
		ValidationMessages = validationMessages;
	}
	
	
}
