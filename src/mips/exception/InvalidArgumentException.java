package mips.exception;

/**
 * Thrown when the argument was in invalid format.
 * 
 * @author Kenichi Maehashi 
 */
public class InvalidArgumentException extends SyntaxException {
	public InvalidArgumentException(int lineNo) {
		super("Invalid argument", lineNo);
	}

	public InvalidArgumentException(String msg, int lineNo) {
		super("Invalid argument (" + msg + ")", lineNo);
	}
}
