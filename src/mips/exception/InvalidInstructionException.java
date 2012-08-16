package mips.exception;

/**
 * Thrown when the instruction was in invalid format.
 * 
 * @author Kenichi Maehashi 
 */
public class InvalidInstructionException extends Exception {
	private final String inststr;
	private final int lineNo;

	public InvalidInstructionException(String inststr, int lineNo) {
		this.inststr = inststr;
		this.lineNo = lineNo;
	}

	@Override
	public String getMessage() {
		return "Invalid instruction \"" + inststr + "\" on line " + lineNo + ".";
	}

}
