package mips.entity;

/**
 * A class representing Register File.
 * 
 * @author Kenichi Maehashi 
 */
public class RegisterFile {
	private final int size;
	private final int[] data;

	/**
	 * Construct new register file.
	 * 
	 * @param size the size of the register file
	 */
	public RegisterFile(int size) {
		this.size = size;
		this.data = new int[size];
	}

	/**
	 * Read data from the register file.
	 * 
	 * @param i the register number
	 * @return the data
	 */
	public int get(int i) {
		return (i == 0) ? 0 : data[i];
	}

	/**
	 * Returns the size of the register.
	 * 
	 * @return the size of the register
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Set a value to the register file.
	 * 
	 * @param i the register number
	 * @param value the data
	 */
	public void set(int i, int value) {
		data[i] = value;
	}
}
