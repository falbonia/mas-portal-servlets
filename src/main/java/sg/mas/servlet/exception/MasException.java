package sg.mas.servlet.exception;

public class MasException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MasException() {
		
	}

	public MasException(String message) {
		super(message);
		
	}

	public MasException(Throwable cause) {
		super(cause);
		
	}

	public MasException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public MasException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

}
