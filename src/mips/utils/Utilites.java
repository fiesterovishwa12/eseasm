package mips.utils;

import java.util.*;
import java.util.regex.*;

import mips.entity.Instruction;
import mips.exception.*;

/**
 * Provides utility methods for the Assembler/Disassembler/Simulator.
 * 
 * @author Kenichi Maehashi 
 */
public class Utilites {

	// Alias names for registers
	private static final String[] regname = new String[] { "zero", "at", "v0", "v1", "a0", "a1", "a2", "a3", "t0",
			"t1", "t2", "t3", "t4", "t5", "t6", "t7", "s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7", "t8", "t9", "k0",
			"k1", "gp", "sp", "fp", "ra" };

	// Binary code format (Altera-MIF)
	private static final Pattern linePattern = Pattern.compile("\\s*([a-zA-Z0-9]+)\\s*:\\s*([a-zA-Z0-9]{8});.*");

	/**
	 * Gets the register number for the given string.
	 * 
	 * @param str
	 *            the register string (e.g., "$2" or "$ra")
	 * @return the register number. null when the register does not exist.
	 */
	public static Integer getRegisterNumber(String str) {
		if (str.startsWith("$")) {
			String reg = str.substring(1); // remove "$"
			try {
				int r = Integer.parseInt(reg, 10);
				if ((0 <= r) && (r < 32)) {
					return r;
				}
			} catch (NumberFormatException e) {
				for (int i = 0; i < regname.length; i++) {
					if (regname[i].equals(reg)) {
						return i;
					}
				}
				return null;
			}
		}
		return null;
	}

	/**
	 * Decodes the binary code assembled by the Assembler.
	 * 
	 * @param src
	 *            the binary code
	 * @return List of Instructions
	 * @throws SyntaxException
	 *             if there was a syntax error in the code
	 * @throws InvalidInstructionException
	 *             if there was a invalid instruction in the code
	 */
	public static List<Instruction> decodeInstruction(String src) throws SyntaxException, InvalidInstructionException {
		List<Instruction> instList = new ArrayList<Instruction>();
		Scanner scanner = new Scanner(src);
		int lineNo = 0;

		while (scanner.hasNext()) {
			lineNo++;
			String line = scanner.nextLine();
			Matcher m = linePattern.matcher(line);
			if (m.matches()) {
				String saddr = m.group(1);
				String sinst = m.group(2);
				Instruction inst = Instruction.createInstruction(sinst, lineNo, Integer.parseInt(saddr, 16) / 4);
				instList.add(inst);
			} else {
				throw new SyntaxException("Invalid format (" + line + ")", lineNo);
			}
		}
		return instList;
	}

	/**
	 * Converts the string (in decimal expression) into integer, ensuring that the value fits in <i>bits</i> bits.
	 * 
	 * @param str
	 *            the string to convert
	 * @param bits
	 *            the binary bits
	 * @return the converted Integer. null when the string does not fit in <i>bits</i> bits.
	 */
	public static Integer dStrToInt(String str, int bits) {
		try {
			int val = Integer.decode(str);
			if (Math.abs(val) < Math.pow(2, bits)) {
				return val;
			}
		} catch (NumberFormatException e) {
			return null;
		}
		return null;
	}

	/**
	 * Converts the string (in binary expression) into integer.
	 * 
	 * @param str
	 *            the string to convert
	 * @param bits
	 *            the binary bits
	 * @return the converted Integer.
	 */
	public static int bStrToInt(String str, int bits) {
		String newStr = str;
		while (newStr.length() < bits) {
			newStr = '0' + newStr;
		}
		boolean minus = (str.charAt(0) == '1');
		long lval = Long.parseLong(str, 2);
		if (minus) {
			return (int) (lval - Math.pow(2, bits));
		} else {
			return (int) lval;
		}
	}

	/**
	 * Converts the integer into binary expression of string, ensuring that the value fits in <i>bits</i> bits.
	 * 
	 * @param data
	 *            the integer to convert
	 * @param bits
	 *            the binary bits
	 * @return the converted string
	 */
	public static String intToBinaryString(int data, int bits) {
		String strData = Integer.toBinaryString(data);
		int offset = strData.length() - bits;

		if (0 < offset) {
			return strData.substring(offset);
		} else if (offset < 0) {
			int supplement = -1 * offset;
			StringBuffer strbuf = new StringBuffer(supplement + 1);
			for (int i = 0; i < supplement; i++) {
				strbuf.append("0");
			}
			strbuf.append(strData);
			return strbuf.toString();
		} else {
			return strData;
		}
	}

	/**
	 * Extends the integer.
	 * 
	 * @param data
	 *            the integer to be extended
	 * @param bits
	 *            the bits of the data
	 * @param zeroExtend
	 *            true for zero-extend, false for sign-extend
	 * @return the extended integer
	 */
	public static int extendInt(int data, int bits, boolean zeroExtend) {
		boolean minus = (!zeroExtend) && ((Math.pow(2, bits) / 2) < data);
		String datastr = Integer.toBinaryString(data);
		while (datastr.length() < bits) {
			datastr = '0' + datastr;
		}
		while (datastr.length() < 32) {
			datastr = (minus ? '1' : '0') + datastr;
		}
		return (int) Long.parseLong(datastr, 2);
	}

	/**
	 * Returns if the string consists of numbers.
	 * 
	 * @param str
	 *            the string to test
	 * @return true if the string consists of number
	 */
	public static boolean isIntegerForm(String str) {
		return str.matches("-?\\d+");
	}

	/**
	 * Returns if all the object is not null.
	 * 
	 * @param objects
	 *            any number of objects to test
	 * @return true if all objects are defined (not null)
	 */
	public static boolean isDefined(Object... objects) {
		for (Object obj : objects) {
			if (obj == null) {
				return false;
			}
		}
		return true;
	}
}
