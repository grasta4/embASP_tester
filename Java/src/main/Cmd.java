package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

final class Cmd {
	private Cmd() {
		
	}
	
	private static final void printLines(final String cmd, InputStream is) throws IOException {
    	String line;
    	final BufferedReader input = new BufferedReader(new InputStreamReader(is));
        
    	while((line = input.readLine()) != null)
        	System.out.println(cmd + " " + line);
    }

    private static final void runProcess(final String command) throws InterruptedException, IOException {
    	final Process process = Runtime.getRuntime().exec(command);
        
    	printLines(command + " stdout:", process.getInputStream());
        printLines(command + " stderr:", process.getErrorStream());
        process.waitFor();
        
        final int exitValue = process.exitValue();
        
        System.out.println(command + " exitValue: " + process.exitValue());
        
        if(exitValue != 0)
        	System.exit(exitValue);
	}
    
    static final void run(final String command) {
    	try {
			runProcess(command);
		} catch (final InterruptedException | IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
}