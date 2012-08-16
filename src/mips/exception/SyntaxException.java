package mips.exception;

/**
 * Thrown when the assembler code contains a syntax error.
 * 
 * @author Kenichi Maehashi 
 */
public class SyntaxException extends Exception {
	private enum ERECORD {
		MSG_LINE_CHAR, MSG_LINE, MSG;
	}

	private final ERECORD record;
	private final String msg;
	private final int line;
	private final int charLoc;

	/**
	 * Constructs new syntax exception with message.
	 * 
	 * @param msg
	 *            the error message
	 */
	public SyntaxException(String msg) {
		super();
		this.record = ERECORD.MSG;
		this.msg = msg;
		this.line = -1;
		this.charLoc = -1;
	}

	/**
	 * Constructs new syntax exception with message and line number.
	 * 
	 * @param msg
	 *            the error message
	 * @param line
	 *            the line number
	 */
	public SyntaxException(String msg, int line) {
		super();
		this.record = ERECORD.MSG_LINE;
		this.msg = msg;
		this.line = line;
		this.charLoc = -1;
	}

	/**
	 * Constructs new syntax exception with message, line number and character location.
	 * 
	 * @param msg
	 *            the error message
	 * @param line
	 *            the line number
	 * @param charLoc
	 *            the character location
	 */
	public SyntaxException(String msg, int line, int charLoc) {
		super();
		this.record = ERECORD.MSG_LINE_CHAR;
		this.msg = msg;
		this.line = line;
		this.charLoc = charLoc;
	}

	/**
	 * Returns the error message.
	 * 
	 * @return the error message
	 */
	@Override
	public String getMessage() {
		StringBuffer strbuf = new StringBuffer(3);
		strbuf.append("Syntax Error: ");
		strbuf.append(msg);

		switch (record) {
		case MSG:
			break;
		case MSG_LINE:
			strbuf.append(" on line " + line);
			break;
		case MSG_LINE_CHAR:
			strbuf.append(" on line " + line + ", at char " + charLoc);
			break;
		}
		strbuf.append(".");

		return strbuf.toString();
	}
}
