package mips;

import java.util.*;

import static mips.utils.Utilites.*;
import mips.entity.*;
import mips.exception.*;

/**
 * MIPS Simulator
 * 
 * @author Kenichi Maehashi 
 */
public class Simulator implements Runnable {
	private List<Instruction> instList; // decoded Instructions will be stored
	private Memory memory; // pairs of address and data will be stored
	private RegisterFile regfile; // register file
	private int pc; // program counter
	private boolean kill; // if set to true, simulation must be killed immediately

	/**
	 * Constructs new Simulator object.
	 */
	public Simulator() {
		init();
	}

	/**
	 * Initializes the Simulator.
	 */
	public void init() {
		instList = new ArrayList<Instruction>();
		memory = new Memory();
		regfile = new RegisterFile(32);
		pc = 0;
		kill = false;
	}

	/**
	 * Decodes the binary code assembled by the Assembler.
	 * 
	 * @param src
	 *            the binary code
	 * @throws SyntaxException
	 *             If there was a syntax error in the binary code
	 * @throws InvalidInstructionException
	 *             If there was a invalid instruction in the binary code
	 */
	public void decode(String src) throws SyntaxException, InvalidInstructionException {
		instList.addAll(decodeInstruction(src));
	}

	/**
	 * Run the previously parsed instructions. Do not call this method directly; you need to use generate new Thread like this: Thread t = new
	 * Thread(new Simulator());
	 */
	public void run() {
		if (instList.size() == 0) {
			return;
		}
		while (!kill) {
			Instruction currentInst;
			try {
				currentInst = instList.get(pc);
			} catch (IndexOutOfBoundsException e) {
				if (pc == instList.size()) {
					return; // end of program
				}
				throw new SimulationException("No instructions here", pc);
			}
			pc = currentInst.run(pc, regfile, memory);
			Thread.yield();
		}
	}

	/**
	 * Set the contents of the memory.
	 * 
	 * @param address
	 *            the address to place data
	 * @param data
	 *            the data
	 */
	public void setMemory(int address, int data) {
		memory.write(address, data);
	}

	/**
	 * Return the current contents of the memory.
	 * 
	 * @param address
	 *            the address
	 * @return the memory
	 */
	public int getMemory(int address) {
		return memory.read(address);
	}

	/**
	 * Return the current contents of the regfile.
	 * 
	 * @return the regfile
	 */
	public RegisterFile getRegfile() {
		return regfile;
	}

	/**
	 * Return the current value of the program counter.
	 * 
	 * @return the program counter
	 */
	public int getPc() {
		return pc;
	}

	/**
	 * Kill the currently running simulation process.
	 */
	public void kill() {
		kill = true;
	}
}
