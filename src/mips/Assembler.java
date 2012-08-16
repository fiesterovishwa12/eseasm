package mips;

import java.util.*;

import static mips.utils.Utilites.*;
import mips.entity.*;
import mips.exception.*;

/**
 * MIPS Assembler
 * 
 * @author Kenichi Maehashi 
 */
public class Assembler {

	// parsed Instructions will be stored
	private List<Instruction> instList;

	// pairs of label and address will be stored
	private Map<String, Integer> labelMap;

	/**
	 * Constructs new Assembler object.
	 */
	public Assembler() {
		init();
	}

	/**
	 * Initializes the Assembler.
	 */
	public void init() {
		instList = new ArrayList<Instruction>();
		labelMap = new HashMap<String, Integer>();
	}

	/**
	 * Parse the assembler source.
	 * 
	 * @param src
	 *            the assembler source
	 * @throws SyntaxException
	 *             If there was a syntax error in the source
	 */
	public void parse(String src) throws SyntaxException {
		Scanner scanner = new Scanner(src);

		// "lineNo" stores the line number of the source code.
		// This includes lines without any codes (e.g. empty or comment-only line).
		// "stepNo" stores the actual step number in contrast.
		int lineNo = 0, stepNo = 0;

		while (scanner.hasNext()) {
			// go to next line
			lineNo++;
			String line = scanner.nextLine();

			// Parse each line in form of [[label:] code args][;comment]
			String code, label, mnemonic;
			String[] args;

			// Remove comments
			int commentLoc = line.indexOf(';');
			if (commentLoc == -1) {
				commentLoc = line.length();
			}
			code = line.substring(0, commentLoc);
			// comment = line.substring(commentLoc);

			// if it was an empty line, continue to the next line
			if (code.trim().equals("")) {
				continue;
			}

			// Split label (empty if it doesn't exist), code and arguments
			String[] codeParts = code.split("[\t ]+", 3);
			// and trim them
			for (int i = 0; i < codeParts.length; i++) {
				codeParts[i] = codeParts[i].trim();
			}

			// The line must be divided into 1 or 3 parts
			if ((codeParts.length != 1) && (codeParts.length != 3)) {
				throw new SyntaxException("No arguments given (maybe you're missing head tab/space?)", lineNo);
			}

			// Process label names
			label = codeParts[0]; // this may be either "<label>:" or empty
			if (label.endsWith(":")) {
				label = label.substring(0, label.length() - 1); // remove ":"
				// label couldn't be a integer as it is confusing with address
				if (isIntegerForm(label)) {
					throw new SyntaxException("Label cannot be a integer (" + label + ")", lineNo);
				}
				// the label is in the correct format, so add it to the label map
				labelMap.put(label, stepNo);
			} else if (!label.equals("")) {
				// not label && not empty
				throw new SyntaxException("Label must be followed by \":\" ( " + label + ")", lineNo);
			}

			// if the line has an actual instruction
			// (otherwise it only has a label)
			if (codeParts.length == 3) {
				Instruction inst;
				mnemonic = codeParts[1];
				// split arguments by comma
				args = codeParts[2].split(",[\t ]*");
				try {
					// try to find mnemonic and create new instance of Instruction
					inst = Instruction.createInstruction(Instruction.getInstByMnemonic(mnemonic), lineNo, stepNo);
				} catch (IllegalArgumentException e) {
					// no such mnemonic
					throw new SyntaxException("Invalid mnemonic (" + mnemonic + ")", lineNo);
				}

				// parse the arguments
				inst.parseArgs(args);
				
				// arguments is in correct format, add it to instruction list
				instList.add(inst);
				
				// increase "stepNo" because there was an instruction
				stepNo++;
			}
		}
	}

	/**
	 * Assembles the previously parsed instructions.
	 * 
	 * @return the assembled binary code (Altera-friendly format)
	 * @throws LabelNotFoundException
	 *             If undefined label was used
	 */
	public String assemble() throws LabelNotFoundException {
		StringBuffer strbuf = new StringBuffer(instList.size());
		for (int i = 0, size = instList.size(); i < size; i++) {
			Instruction inst = instList.get(i);
			String memcnt = (i < 16 ? " " : "") + Integer.toHexString(i).toUpperCase();
			String addr = (i * 4 < 16 ? "0" : "") + Integer.toHexString(i * 4).toUpperCase();
			String binary = inst.toHexString(labelMap);
			strbuf.append(memcnt + " :     " + binary + "; % (" + addr + ") %\n");
		}
		return strbuf.toString();
	}
}
