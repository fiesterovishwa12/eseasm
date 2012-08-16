package mips.exception;

/**
 * Thrown when some error occurred in the simulation process.
 * 
 * @author Kenichi Maehashi 
 */
public class SimulationException extends RuntimeException {
	private final String msg;
	private final int pc;

	/**
	 * Constructs new exception with message and pc.
	 * 
	 * @param msg
	 *             the error message
	 * @param pc
	 *            the program counter
	 */
	public SimulationException(String msg, int pc) {
		super();
		this.msg = msg;
		this.pc = pc;
	}
	
	@Override
	public String getMessage() {
		return "Simulation Error: " + msg + " at step " + pc + ".";
	}
}
