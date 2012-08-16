package mips.exception;

/**
 * Thrown when the instrucion is trying to jump to an undefined label.
 * 
 * @author Kenichi Maehashi 
 */
public class LabelNotFoundException extends Exception {
	private final String label;
	private final int line;

	/**
	 * Constructs new exception with the label and line number.
	 * 
	 * @param label
	 *            the label
	 * @param line
	 *            the line number
	 */
	public LabelNotFoundException(String label, int line) {
		super();
		this.label = label;
		this.line = line;
	}

	@Override
	public String getMessage() {
		return "Assembly Error: Label \"" + label + "\" not found on line " + line + ".";
	}
}
