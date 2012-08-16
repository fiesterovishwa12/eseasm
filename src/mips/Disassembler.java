package mips;

import java.util.*;

import static mips.utils.Utilites.*;
import mips.entity.*;
import mips.exception.*;

/**
 * MIPS Disassembler
 * 
 * @author Kenichi Maehashi 
 */
public class Disassembler {
	// parsed Instructions will be stored
	private List<Instruction> instList;

	/**
	 * Constructs new Disassembler object.
	 */
	public Disassembler() {
		init();
	}

	/**
	 * Initializes the Disassembler.
	 */
	public void init() {
		instList = new ArrayList<Instruction>();
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
	 * Disassembles the previously parsed instructions.
	 * 
	 * @return the assembler source
	 */
	public String disassemble() {
		StringBuffer strbuf = new StringBuffer(instList.size() * 2);
		for (Instruction inst : instList) {
			strbuf.append(inst.toCode());
			strbuf.append("\n");
		}
		return strbuf.toString();
	}
}
