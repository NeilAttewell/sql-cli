package sqlclient.core.exceptions;

/**
 * @author Neil Attewell
 */
public class ExitException extends Exception{
	public ExitException() {
	}
	public ExitException(Exception exception) {
		super(exception);
	}
}
