package is.ru.gapl.exception;

/**
 * This exception is thrown if the
 * play time is over.
 * 
 * @author SFW GROUP
 *
 */
public class PlayTimeOverException extends Exception {
	
	private static final long serialVersionUID = 5151349419496626186L;

	/**
	 * Empty constructor
	 */
	public PlayTimeOverException() {
		super("Your paly time is over!");
	}

	/**
	 * Constructor to provide your own
	 * message
	 * 
	 * @param msg the message to display
	 */
	public PlayTimeOverException(String msg) {
	    super(msg);
	 }
}
