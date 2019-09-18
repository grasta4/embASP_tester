package main;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;
import it.unical.mat.embasp.base.OptionDescriptor;
import it.unical.mat.embasp.languages.IllegalAnnotationException;
import it.unical.mat.embasp.languages.ObjectNotValidException;
import it.unical.mat.embasp.languages.asp.AnswerSets;
import it.unical.mat.embasp.languages.asp.ASPInputProgram;
import it.unical.mat.embasp.languages.asp.ASPMapper;
import it.unical.mat.embasp.platforms.desktop.DesktopHandler;
import it.unical.mat.embasp.platforms.desktop.DesktopService;
import it.unical.mat.embasp.specializations.clingo.desktop.ClingoDesktopService;
import it.unical.mat.embasp.specializations.dlv.desktop.DLVDesktopService;
//import it.unical.mat.embasp.specializations.dlv2.desktop.DLV2DesktopService;

public class ASPTest {
	private static final String EXECUTION_TIMES_PATH = "files/executionTimes.csv";
	
	static enum Solver {
		CLINGO, DLV, /*DLV2*/;
		
		String getOutputOption() {
			switch(this) {
				case CLINGO: return "--verbose=0";
				case DLV: return "-silent";
				//case DLV2: return "--competition-output";
				default: return null;
			}
		}
		
		DesktopService getService() {
			switch(this) {
				case CLINGO: return new ClingoDesktopService("rsc/clingo.solver");
				case DLV: return new DLVDesktopService("rsc/dlv.solver");
				//case DLV2: return new DLV2DesktopService("rsc/dlv2.solver");
				default: return null;
			}
		}
	}
	
	private static final void clearDir(final String dirPath) {
		final Path path = Paths.get(dirPath);
		
		if(Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS))
			try(final DirectoryStream <Path> entries = Files.newDirectoryStream(path)) {
				for(Path entry : entries)
					Files.delete(entry);
			} catch (final IOException e) {
				e.printStackTrace();
			}
	}
	
	private static final void loadClasses(final ASPMapper mapper) throws IOException {
		try(final Stream<Path> paths = Files.walk(Paths.get("files/_classes"))) {
		    paths.filter(Files::isRegularFile).forEach(file -> {
		    	try(final URLClassLoader loader = new URLClassLoader(new URL[] {file.getParent().toUri().toURL()})) {
	    			final String fullClassName = file.getFileName().toString();
	    			
	    			mapper.registerClass(loader.loadClass(fullClassName.substring(0, fullClassName.lastIndexOf('.'))));
				} catch(ClassNotFoundException | IllegalAnnotationException | IOException | ObjectNotValidException e) {
					e.printStackTrace();
				}
		    });
		}
	}
	
	private static final TreeSet <String> sortFacts(final Set <Object> answerSet) {
		final TreeSet <String> sorted = new TreeSet <> ();
		
		answerSet.forEach(atom -> sorted.add(atom.toString()));
		
		return sorted;
	}
	
	private static final void writeToFile(final String file, final String string, final Boolean newLineBegin, final boolean semicolon) {
		String tmp = string + (semicolon ? ';' : "");
		
		if(newLineBegin != null)
			tmp = newLineBegin ? '\n' + string + (semicolon ? ';' : "") : string + (semicolon ? ";\n" : '\n');
		
		try {
			Files.write(Paths.get(file), tmp.getBytes(), StandardOpenOption.APPEND);
		}catch(final IOException e) {
		    e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		if(args.length <= 1) {
			System.out.println("USAGE: ASPTest input_file class1 [class2...]");
			System.exit(0);
		}
		
		for(int i = 1; i < args.length; i++)
			Cmd.run("javac -cp lib/embASP.jar -d files/_classes files/" + args[i]);
		
		try {
			loadClasses(ASPMapper.getInstance());
			writeToFile(EXECUTION_TIMES_PATH, args[0], true, true);
		} catch(final IOException e) {
			e.printStackTrace();
		}
		
		for(final Solver solver : Solver.values()) {
			final ASPInputProgram inputProgram = new ASPInputProgram();
			final DesktopHandler handler = new DesktopHandler(solver.getService());
			final LinkedList <Set <Object>> answerSets = new LinkedList <> ();
			final int optionId = handler.addOption(new OptionDescriptor(solver.getOutputOption()));
			
			handler.addProgram(inputProgram);
			inputProgram.addFilesPath("files/" + args[0]);
			
			final LinkedList <String> sortedOutput = OutputManager.processRawOutput(solver.name(), ((AnswerSets)(handler.startSync())).getAnswerSetsString());
			
			handler.removeOption(optionId);
			
			final long start = System.nanoTime();
			
			((AnswerSets)(handler.startSync())).getAnswersets().forEach(answerSet -> {
				try {
					answerSets.add(answerSet.getAtoms());
				} catch(final IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException | NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				}
			});
			
			final long end = System.nanoTime();
			
			writeToFile(EXECUTION_TIMES_PATH, ((Long)((end - start) / 1000000)).toString(), null, true);
			
			if(!answerSets.isEmpty())
				answerSets.forEach(answerSet -> {
					final String tmp = String.join(" ", sortFacts(answerSet));
					
					if(!sortedOutput.contains(tmp))
						System.out.println("ERROR! Original " + solver.name() + " output does not contain:\n" + tmp + "\n");
				});
		}
		
		clearDir("files/_classes");
	}
}