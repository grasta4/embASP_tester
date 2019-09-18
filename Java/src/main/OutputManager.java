package main;

import java.util.Arrays;
import java.util.LinkedList;

final class OutputManager {
	private OutputManager() {
		
	}
	
	static final LinkedList <String> processRawOutput(final String solver, final String rawOutput) {
		if(solver.equalsIgnoreCase("DLV"))
			return processDLVOutput(rawOutput);
		else
			return processClingo_DLV2Output(rawOutput);
	}
	
	private static final LinkedList <String> processDLVOutput(final String rawOutput) {
		final LinkedList <String> sortedOutput = new LinkedList <> ();

		for(final String str: rawOutput.split("\\r?\\n")) {
			final String[] tokens = str.substring(1, str.length() - 1).split(",\\s+");
			
			Arrays.sort(tokens);
			sortedOutput.add(String.join(" ", tokens));
		}

		return sortedOutput;
	}
	
	private static final LinkedList <String> processClingo_DLV2Output(final String rawOutput) {
		final LinkedList <String> sortedOutput = new LinkedList <> ();

		for(final String str: rawOutput.split("\\r?\\n")) {
			final String[] tokens = str.split("\\s+");
			
			Arrays.sort(tokens);
			sortedOutput.add(String.join(" ", tokens));
		}
		
		return sortedOutput;
	}
}
