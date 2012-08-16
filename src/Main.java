import java.io.*;
import java.util.*;

import mips.*;
import mips.entity.*;
import mips.exception.*;

/**
 * Sample Code for MIPS Assembler/Disassembler/Simulator
 * 
 * @author Kenichi Maehashi 
 */
public class Main {
	/**
	 * Main entry point.
	 * 
	 * @param args
	 *            The assembly source file (only the first argument is used). If no arguments are given, the default file is used.
	 */
	public static void main(String[] args) {
		String sourceCode, assembledCode, disassembledCode, reassembledCode;
		String filePath = args.length > 0 ? args[0] : "multiplication.s";
		Assembler assembler = new Assembler();
		Disassembler disassembler = new Disassembler();
		Simulator simulator = new Simulator();

		// ////////////// Load File //////////////
		try {
			sourceCode = loadFile(filePath);
		} catch (FileNotFoundException e) {
			System.err.println("File not found: " + filePath);
			return;
		}
		System.out.println("===== Loaded File =====");
		System.out.println(sourceCode);

		// ////////////// Assembler //////////////
		try {
			assembler.parse(sourceCode);
		} catch (SyntaxException e) {
			System.err.println(e.getMessage());
			return;
		}
		try {
			assembledCode = assembler.assemble();
		} catch (LabelNotFoundException e) {
			System.err.println(e.getMessage());
			return;
		}
		System.out.println("===== Assembly Result =====");
		System.out.println(assembledCode);

		// ////////////// Disassembler //////////////
		try {
			disassembler.decode(assembledCode);
		} catch (SyntaxException e) {
			System.err.println(e.getMessage());
			return;
		} catch (InvalidInstructionException e) {
			System.err.println(e.getMessage());
			return;
		}
		disassembledCode = disassembler.disassemble();
		System.out.println("===== Disassembly Result =====");
		System.out.println(disassembledCode);

		// ////////////// Re-Assemble //////////////
		Assembler assembler2 = new Assembler();
		try {
			assembler2.parse(disassembledCode);
		} catch (SyntaxException e) {
			System.err.println(e.getMessage());
			return;
		}
		try {
			reassembledCode = assembler2.assemble();
		} catch (LabelNotFoundException e) {
			System.err.println(e.getMessage());
			return;
		}
		System.out.println("===== Re-Assembly Result =====");
		System.out.println(reassembledCode);

		// ////////////// Simulator //////////////
		try {
			simulator.decode(assembledCode);
		} catch (SyntaxException e) {
			System.err.println(e.getMessage());
			return;
		} catch (InvalidInstructionException e) {
			System.err.println(e.getMessage());
			return;
		}
		simulator.setMemory(0, 5);
		simulator.setMemory(4, 7);
		Thread t = new Thread(simulator);
		t.start();
		try {
			t.join(1000); // wait for 1 second
			if (t.isAlive()) {
				// still running, wait for 3 seconds
				System.err.println("Simulation is running, will be killed in 3 seconds...");
				t.join(3000);
				simulator.kill();
			}
		} catch (InterruptedException e) {
			return;
		}
		System.out.println("===== Simulation Result =====");
		System.out.println("PC = " + simulator.getPc() + " * 4");
		RegisterFile regfile = simulator.getRegfile();
		for (int i = 0, size = regfile.getSize(); i < size; i++) {
			System.out.println("Regfile[" + i + "] = " + regfile.get(i));
		}
	}

	private static String loadFile(String filePath) throws FileNotFoundException {
		Scanner scanner = new Scanner(new File(filePath));
		StringBuffer strbuf = new StringBuffer();
		while (scanner.hasNext()) {
			strbuf.append(scanner.nextLine());
			strbuf.append("\n");
		}
		return strbuf.toString();
	}
}
