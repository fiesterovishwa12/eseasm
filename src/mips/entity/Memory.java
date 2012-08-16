package mips.entity;

import java.util.Map;
import java.util.HashMap;

/**
 * A class representing Memory.
 * 
 * @author Kenichi Maehashi 
 */
public class Memory {
	private Map<Integer, Integer> mem = new HashMap<Integer, Integer>();

	/**
	 * Read a data of the memory.
	 * 
	 * @param addr
	 *            the memory address to read
	 * @return the data at the address
	 */
	public int read(int addr) {
		int data = mem.get(addr);
		return (data == 0) ? 0 : data;
	}

	/**
	 * Write a data to the memory.
	 * 
	 * @param addr
	 *            the memory address to write data
	 * @param data
	 *            the data to write
	 * @return the previous value at the address
	 */
	public int write(int addr, int data) {
		Integer oldData = mem.put(addr, data);
		return (oldData == null) ? 0 : oldData;
	}
}
