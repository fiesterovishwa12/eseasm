package mips.entity;

import java.util.*;

import static mips.utils.Utilites.*;
import mips.exception.*;

/**
 * A class representing Instructions.
 * 
 * @author Kenichi Maehashi 
 */
public class Instruction {
	/**
	 * Represents each kind of supported MIPS instructions.
	 */
	private static enum INST {
		/**
		 * ADD (R-type)
		 */
		ADD(0, 32),
		/**
		 * SUB (R-type)
		 */
		SUB(0, 34),
		/**
		 * AND (R-type)
		 */
		AND(0, 36),
		/**
		 * OR (R-type)
		 */
		OR(0, 37),
		/**
		 * XOR (R-type)
		 */
		XOR(0, 38),
		/**
		 * SLL (R-type)
		 */
		SLL(0, 0),
		/**
		 * SRL (R-type)
		 */
		SRL(0, 2),
		/**
		 * SRA (R-type)
		 */
		SRA(0, 3),
		/**
		 * JR (R-type)
		 */
		JR(0, 8),
		/**
		 * ADDI (I-type)
		 */
		ADDI(8, null),
		/**
		 * ANDI (I-type)
		 */
		ANDI(12, null),
		/**
		 * ORI (I-type)
		 */
		ORI(13, null),
		/**
		 * XORI (I-type)
		 */
		XORI(14, null),
		/**
		 * LW (I-type)
		 */
		LW(35, null),
		/**
		 * SW (I-type)
		 */
		SW(43, null),
		/**
		 * BEQ (I-type)
		 */
		BEQ(4, null),
		/**
		 * BNE (I-type)
		 */
		BNE(5, null),
		/**
		 * LUI (I-type)
		 */
		LUI(15, null),
		/**
		 * J (J-type)
		 */
		J(2, null),
		/**
		 * JAL (J-type)
		 */
		JAL(3, null);

		// opcode for the instruction
		private final Integer opcode;

		// function code for the instruction (R-type instructions only; null for the others)
		private final Integer function;

		private INST(Integer opcode, Integer function) {
			this.opcode = opcode;
			this.function = function;
		}

		/**
		 * Returns opcode.
		 * 
		 * @return opcode for this instruction
		 */
		public Integer getOpcode() {
			return this.opcode;
		}

		/**
		 * Returns function.
		 * 
		 * @return function code for this instruction (null if the instruction is not a R-type)
		 */
		public Integer getFunction() {
			return this.function;
		}
	}

	private final INST inst;
	private final int lineNo;
	private final int stepNo;

	private Integer rs = 0;
	private Integer rt = 0;
	private Integer rd = 0;
	private Integer sa = 0;
	private Integer immediate = 0;
	private Integer address = 0;
	private String jumpto;

	/**
	 * Constructs new Instruction object.
	 * 
	 * @param inst
	 *            the instruction kind
	 * @param lineNo
	 *            line number for the current instruction
	 * @param stepNo
	 *            step number for the current instruction
	 */
	private Instruction(INST inst, int lineNo, int stepNo) {
		this.inst = inst;
		this.lineNo = lineNo;
		this.stepNo = stepNo;
	}

	/**
	 * Get INST object from the mnemonic.
	 * 
	 * @param mnemonic
	 *            the mnemonic
	 * @return the INST object for the mnemonic
	 */
	public static INST getInstByMnemonic(String mnemonic) {
		return INST.valueOf(mnemonic.toUpperCase());
	}

	/**
	 * Creates new Instruction object with the parameter.
	 * 
	 * @param inst
	 *            the instruction kind
	 * @param lineNo
	 *            line number for the current instruction
	 * @param stepNo
	 *            step no for the current instruction
	 * @return the new Instruction object
	 */
	public static Instruction createInstruction(INST inst, int lineNo, int stepNo) {
		return new Instruction(inst, lineNo, stepNo);
	}

	/**
	 * Creates new Instruction object from binary code.
	 * 
	 * @param hexexp
	 *            the binary code in hexadecimal expression (exactly in 8 chars)
	 * @param lineNo
	 *            line no for the current instruction
	 * @param stepNo
	 *            step no for the current instruction
	 * @return the new Instruction object
	 * @throws InvalidInstructionException
	 *             If hexexp was a invalid instruction
	 */
	public static Instruction createInstruction(String hexexp, int lineNo, int stepNo)
			throws InvalidInstructionException {
		INST inst;
		int newOp, newRs, newRt, newRd, newSa, newFunc, newImm, newAddr;

		// Check if the code is in the correct format
		if (!hexexp.matches("^[a-zA-Z0-9]{8}$")) {
			throw new InvalidInstructionException(hexexp, lineNo);
		}

		// Decode it as hex and convert it into binary expression
		String binexp = Long.toBinaryString(Long.decode("0x" + hexexp));

		// Supplement 0
		while (binexp.length() < 32) {
			binexp = "0" + binexp;
		}

		// Split binexp into each part of instruction
		newOp = Integer.parseInt(binexp.substring(0, 6), 2);
		newRs = Integer.parseInt(binexp.substring(6, 11), 2);
		newRt = Integer.parseInt(binexp.substring(11, 16), 2);
		newRd = Integer.parseInt(binexp.substring(16, 21), 2);
		newSa = Integer.parseInt(binexp.substring(21, 26), 2);
		newFunc = Integer.parseInt(binexp.substring(26, 32), 2);
		newImm = bStrToInt(binexp.substring(16, 32), 16);
		newAddr = bStrToInt(binexp.substring(6, 32), 26);	
		
		// Look for the instruction from the current op and func
		inst = null;
		for (INST newInst : INST.values()) {
			if (newOp == newInst.getOpcode()) {
				Integer newInstFunc = newInst.getFunction();
				if ((newInstFunc == null) || (newInstFunc == newFunc)) {
					inst = newInst;
				}
			}
		}

		// No such mnemonic
		if (inst == null) {
			throw new InvalidInstructionException(hexexp, lineNo);
		}

		Instruction instruction = new Instruction(inst, lineNo, stepNo);

		switch (inst) {
		case ADD:
		case SUB:
		case AND:
		case OR:
		case XOR:
		case SLL:
		case SRL:
		case SRA:
		case JR:
			// R-Type
			instruction.rs = newRs;
			instruction.rt = newRt;
			instruction.rd = newRd;
			instruction.sa = newSa;
			break;

		case ADDI:
		case ANDI:
		case ORI:
		case XORI:
		case LW:
		case SW:
		case BEQ:
		case BNE:
		case LUI:
			// I-Type
			instruction.rs = newRs;
			instruction.rt = newRt;
			instruction.immediate = newImm;
			break;

		case J:
		case JAL:
			// J-Type
			instruction.address = newAddr;
			break;
		}

		return instruction;
	}

	/**
	 * Converts the current instruction into the assembler code.
	 * 
	 * @return the assembler code
	 */
	public String toCode() {
		StringBuffer strbuf = new StringBuffer(4);

		strbuf.append("\t" + inst.toString().toLowerCase() + "\t");

		switch (inst) {
		case ADD:
		case SUB:
		case AND:
		case OR:
		case XOR:
			// $rd, $rs, $rt
			strbuf.append("$" + rd + ", ");
			strbuf.append("$" + rs + ", ");
			strbuf.append("$" + rt);
			break;

		case SLL:
		case SRL:
		case SRA:
			// $rd, $rt, sa
			strbuf.append("$" + rd + ", ");
			strbuf.append("$" + rt + ", ");
			strbuf.append(sa);
			break;

		case JR:
			// $rs
			strbuf.append("$" + rs);
			break;

		case ADDI:
			// $rt, $rs, imm
			strbuf.append("$" + rt + ", ");
			strbuf.append("$" + rs + ", ");
			strbuf.append(extendInt(immediate, 16, false));
			break;

		case ANDI:
		case ORI:
		case XORI:
			// $rt, $rs, imm
			strbuf.append("$" + rt + ", ");
			strbuf.append("$" + rs + ", ");
			strbuf.append(extendInt(immediate, 16, true));
			break;

		case LW:
		case SW:
			// $rt, imm($rs)
			strbuf.append("$" + rt + ", ");
			strbuf.append(extendInt(immediate, 16, false) + "($" + rs + ")");
			break;

		case BEQ:
		case BNE:
			// $rs, $rt, label (or imm. address)
			strbuf.append("$" + rs + ", ");
			strbuf.append("$" + rt + ", ");
			if (jumpto == null) {
				strbuf.append(extendInt(immediate, 16, false));
			} else {
				strbuf.append(jumpto);
			}

			break;

		case LUI:
			// $rt, imm
			strbuf.append("$" + rt + ", ");
			strbuf.append(immediate);
			break;

		case J:
		case JAL:
			// label (or address)
			if (jumpto == null) {
				strbuf.append(address);
			} else {
				strbuf.append(jumpto);
			}
			break;
		}
		return strbuf.toString();
	}

	/**
	 * Parse arguments of the assembler code and set the values to the current instruction.
	 * 
	 * @param args
	 *            the array of arguments (e.g., {"$1", "$2", "4"})
	 * @return true if the arguments are parsed successfully
	 * @throws InvalidArgumentException
	 *             If arguments contain a syntax error
	 */
	public void parseArgs(String[] args) throws InvalidArgumentException {
		int argc = 0;

		try {
			switch (inst) {
			case ADD:
			case SUB:
			case AND:
			case OR:
			case XOR:
				// $rd, $rs, $rt
				argc = 3;
				rd = getRegisterNumber(args[0]);
				rs = getRegisterNumber(args[1]);
				rt = getRegisterNumber(args[2]);

				if (!isDefined(rd, rs, rt)) {
					throw new InvalidArgumentException(lineNo);
				}
				break;

			case SLL:
			case SRL:
			case SRA:
				// $rd, $rt, sa
				argc = 3;
				rd = getRegisterNumber(args[0]);
				rt = getRegisterNumber(args[1]);
				sa = dStrToInt(args[2], 5);
				if (!isDefined(rd, rt, sa)) {
					throw new InvalidArgumentException(lineNo);
				}
				break;

			case JR:
				// $rs
				argc = 1;
				rs = getRegisterNumber(args[0]);
				if (!isDefined(rs)) {
					throw new InvalidArgumentException(lineNo);
				}
				break;

			case ADDI:
			case ANDI:
			case ORI:
			case XORI:
				// $rt, $rs, imm
				argc = 3;
				rt = getRegisterNumber(args[0]);
				rs = getRegisterNumber(args[1]);
				immediate = dStrToInt(args[2], 16);
				if (!isDefined(rt, rs, immediate)) {
					throw new InvalidArgumentException(lineNo);
				}
				break;

			case LW:
			case SW:
				// $rt, imm($rs)
				argc = 2;
				rt = getRegisterNumber(args[0]);
				if (args[1].matches("^.+\\(\\$\\d{1,2}\\)$")) {
					int rsloc = args[1].indexOf('(');
					rs = getRegisterNumber(args[1].substring(rsloc + 1, args[1].indexOf(')')));
					immediate = dStrToInt(args[1].substring(0, rsloc), 16);
				}
				if (!isDefined(rt, rs, immediate)) {
					throw new InvalidArgumentException(lineNo);
				}
				break;

			case BEQ:
			case BNE:
				// $rs, $rt, label (or imm. address)
				argc = 3;
				rs = getRegisterNumber(args[0]);
				rt = getRegisterNumber(args[1]);
				if (isIntegerForm(args[2])) {
					immediate = dStrToInt(args[2], 16);
					if (!isDefined(immediate)) {
						throw new InvalidArgumentException(args[2], lineNo);
					}
				} else {
					jumpto = args[2];
					if (!isDefined(jumpto)) {
						throw new InvalidArgumentException(args[2], lineNo);
					}
				}
				if (!isDefined(rt, rs)) {
					throw new InvalidArgumentException(lineNo);
				}

				break;

			case LUI:
				// $rt, imm
				argc = 2;
				rt = getRegisterNumber(args[0]);
				immediate = dStrToInt(args[1], 16);
				if (!isDefined(rt, immediate)) {
					throw new InvalidArgumentException(lineNo);
				}
				break;

			case J:
			case JAL:
				// label (or address)
				argc = 1;
				if (isIntegerForm(args[0])) {
					address = dStrToInt(args[0], 26);
					if (!isDefined(address)) {
						throw new InvalidArgumentException(args[0], lineNo);
					}
				} else {
					jumpto = args[0];
					if (!isDefined(jumpto)) {
						throw new InvalidArgumentException(args[0], lineNo);
					}
				}
				break;
			}

			if (args.length != argc) {
				throw new InvalidArgumentException("Too many arguments; " + argc
						+ " argument(s) are expected, but found " + args.length, lineNo);
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new InvalidArgumentException("Too few arguments; " + argc + " arguments are expected, but found " + args.length + " arguments", lineNo);
		}
	}

	/**
	 * Converts the current instruction into hexadecimal expression.
	 * 
	 * @param labelMap
	 *            the label map
	 * @return the hexadecimal expression of the instruction
	 * @throws LabelNotFoundException
	 *             If the instruction is trying to jump to undefined label
	 */
	public String toHexString(Map<String, Integer> labelMap) throws LabelNotFoundException {
		String str = Long.toHexString(Long.parseLong(toBinaryString(labelMap, ""), 2));
		while (str.length() < 8) {
			str = "0" + str;
		}
		return str;
	}

	/**
	 * Converts the current instruction into binary expression.
	 * 
	 * @param labelMap
	 *            the label map
	 * @return the binary expression of the instruction
	 * @throws LabelNotFoundException
	 *             If the instruction is trying to jump to undefined label
	 */
	public String toBinaryString(Map<String, Integer> labelMap) throws LabelNotFoundException {
		return toBinaryString(labelMap, " ");
	}

	/**
	 * Converts the current instruction into binary expression.
	 * 
	 * @param labelMap
	 *            the label map
	 * @param separator
	 *            the String which is used to split each part of instruction
	 * @return the binary expression of the instruction, separated by the separator
	 * @throws LabelNotFoundException
	 *             If the instruction is trying to jump to undefined label
	 */
	public String toBinaryString(Map<String, Integer> labelMap, String separator) throws LabelNotFoundException {
		StringBuffer strbuf = new StringBuffer(6);
		strbuf.append(intToBinaryString(inst.getOpcode(), 6) + separator);

		switch (inst) {
		case ADD:
		case SUB:
		case AND:
		case OR:
		case XOR:
		case SLL:
		case SRL:
		case SRA:
		case JR:
			strbuf.append(intToBinaryString(rs, 5) + separator);
			strbuf.append(intToBinaryString(rt, 5) + separator);
			strbuf.append(intToBinaryString(rd, 5) + separator);
			strbuf.append(intToBinaryString(sa, 5) + separator);
			strbuf.append(intToBinaryString(inst.getFunction(), 6));
			break;

		case ADDI:
		case ANDI:
		case ORI:
		case XORI:
		case LW:
		case SW:
		case LUI:
			strbuf.append(intToBinaryString(rs, 5) + separator);
			strbuf.append(intToBinaryString(rt, 5) + separator);
			strbuf.append(intToBinaryString(immediate, 16));
			break;

		case BEQ:
		case BNE:
			strbuf.append(intToBinaryString(rs, 5) + separator);
			strbuf.append(intToBinaryString(rt, 5) + separator);
			if (jumpto == null) {
				strbuf.append(intToBinaryString(immediate, 16));
			} else {
				Integer jumpAddr = labelMap.get(jumpto);
				if (jumpAddr == null) {
					throw new LabelNotFoundException(jumpto, lineNo);
				}
				strbuf.append(intToBinaryString(jumpAddr - 1 - stepNo, 16));
			}
			break;

		case J:
		case JAL:
			if (jumpto == null) {
				strbuf.append(intToBinaryString(address, 26));
			} else {
				Integer jumpAddr = labelMap.get(jumpto);
				if (jumpAddr == null) {
					throw new LabelNotFoundException(jumpto, lineNo);
				}
				strbuf.append(intToBinaryString(jumpAddr, 26));
			}
			break;

		}
		return strbuf.toString();
	}

	/**
	 * Run the instruction.
	 * 
	 * @param pc
	 *            the current program counter
	 * @param regfile
	 *            the register file
	 * @param memory
	 *            the memory
	 * @return the next program counter
	 */
	public int run(int pc, RegisterFile regfile, Memory memory) {
		int newPc = pc;
		boolean npc = true; // auto increment pc
		switch (inst) {
		case ADD:
			regfile.set(rd, regfile.get(rs) + regfile.get(rt));
			break;
		case SUB:
			regfile.set(rd, regfile.get(rs) - regfile.get(rt));
			break;
		case AND:
			regfile.set(rd, regfile.get(rs) & regfile.get(rt));
			break;
		case OR:
			regfile.set(rd, regfile.get(rs) | regfile.get(rt));
			break;
		case XOR:
			regfile.set(rd, regfile.get(rs) ^ regfile.get(rt));
			break;
		case SLL:
			regfile.set(rd, regfile.get(rt) << sa);
			break;
		case SRL:
			regfile.set(rd, regfile.get(rt) >> sa);
			break;
		case SRA:
			regfile.set(rd, regfile.get(rt) >>> sa);
			break;
		case JR:
			newPc = regfile.get(rs);
			npc = false;
			break;
		case ADDI:
			regfile.set(rt, regfile.get(rs) + immediate);
			break;
		case ANDI:
			regfile.set(rt, regfile.get(rs) & immediate);
			break;
		case ORI:
			regfile.set(rt, regfile.get(rs) | immediate);
			break;
		case XORI:
			regfile.set(rt, regfile.get(rs) ^ immediate);
			break;
		case LW:
			regfile.set(rt, memory.read(regfile.get(rs) + immediate));
			break;
		case SW:
			memory.write(regfile.get(rs) + immediate, regfile.get(rt));
			break;
		case BEQ:
			if (regfile.get(rs) == regfile.get(rt)) {
				newPc += immediate;
			}
			break;
		case BNE:
			if (regfile.get(rs) != regfile.get(rt)) {
				newPc += immediate;
			}
			break;
		case LUI:
			regfile.set(rt, immediate << 16);
			break;

		case JAL:
			regfile.set(31, newPc + 1);
			// NO BREAK HERE

		case J:
			newPc = (int) ((newPc + 1) & 4026531840L) + ((address << 2) / 4);
			npc = false;
			break;
		}
		if (npc) {
			newPc += 1;
		}
		return newPc;
	}

	/**
	 * Returns a string representation of the instruction.
	 * 
	 * @return a string representation of this instruction
	 */
	@Override
	public String toString() {
		StringBuffer strbuf = new StringBuffer();
		strbuf.append(inst);
		strbuf.append(": {");
		strbuf.append("lineNo: " + lineNo + ", ");
		strbuf.append("stepNo: " + stepNo + ", ");
		strbuf.append("rs: " + rs + ", ");
		strbuf.append("rt: " + rt + ", ");
		strbuf.append("rd: " + rd + ", ");
		strbuf.append("sa: " + sa + ", ");
		strbuf.append("immediate: " + immediate + ", ");
		strbuf.append("address: " + address + ", ");
		strbuf.append("jumpto: " + jumpto);
		strbuf.append("}");
		return strbuf.toString();
	}
}
