package sg.mas.servlet.validation.reader;

public class ValidationMessageWriterException extends Exception {

  private static final long serialVersionUID = 1L;

  public ValidationMessageWriterException(final Exception e) {
    super(e);
  }

  public ValidationMessageWriterException(final String string) {
    super(string);
  }

}
