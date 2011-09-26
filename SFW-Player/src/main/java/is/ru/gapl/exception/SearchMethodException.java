package is.ru.gapl.exception;

/**
 * 
 * @author SFW GROUP
 *
 */
public class SearchMethodException extends Exception {

	private static final long serialVersionUID = 5888245972604664781L;

	/**
	 * Empty constructor
	 */
	public SearchMethodException() {
		super("Search Method Exception!");
	}

	/**
	 * Constructor to provide your own
	 * message
	 * 
	 * @param msg the message to display
	 */
	public SearchMethodException(String msg) {
	    super(msg);
	 }
}
