package main;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import it.unical.mat.embasp.specializations.dlv2.desktop.DLV2DesktopService;

public final class ASPTest {
	static enum Solver {
		CLINGO, DLV, DLV2;
		
		String getOutputOption() {
			switch(this) {
				case CLINGO: return "--verbose=0";
				case DLV: return "-silent";
				case DLV2: return "--competition-output";
				default: return null;
			}
		}
		
		DesktopService getService(final String solversFolder) {
			switch(this) {
				case CLINGO: return new ClingoDesktopService(solversFolder + "clingo.solver");
				case DLV: return new DLVDesktopService(solversFolder + "dlv.solver");
				case DLV2: return new DLV2DesktopService(solversFolder + "dlv2.solver");
				default: return null;
			}
		}
	}
	
	public static final void importClasses(final String[] classes) throws IOException {
		for(final String cls : classes)
			Cmd.run("javac -cp lib/embASP.jar -d " + FileManager.CLASSES_PATH + " " + cls);
		
		try(final Stream<Path> paths = Files.walk(Paths.get(FileManager.CLASSES_PATH))) {
		    paths.filter(Files::isRegularFile).forEach(file -> {
		    	try(final URLClassLoader loader = new URLClassLoader(new URL[] {file.getParent().toUri().toURL()})) {
	    			final String fullClassName = file.getFileName().toString();
	    			
	    			ASPMapper.getInstance().registerClass(loader.loadClass(fullClassName.substring(0, fullClassName.lastIndexOf('.'))));
				} catch(ClassNotFoundException | IllegalAnnotationException | IOException | ObjectNotValidException e) {
					e.printStackTrace();
					System.exit(-1);
				}
		    });
		}
	}
	
	public static final void loadClasses(final Class<?>[] classes) throws IllegalAnnotationException, ObjectNotValidException {
		for(final Class<?> cls : classes)
			ASPMapper.getInstance().registerClass(cls);
	}
	
	private static final TreeSet <String> sortFacts(final Set <Object> answerSet) {
		final TreeSet <String> sorted = new TreeSet <> ();
		
		answerSet.forEach(atom -> sorted.add(atom.toString()));
		
		return sorted;
	}
	
	public static void run(final String csvFile, final String solversDir, final String inputFile, final String optionFile) {
		FileManager.writeToFile(csvFile, inputFile, true, true);
		
		for(final Solver solver : Solver.values()) {
			if(!FileManager.solverPresent(solversDir, solver.name().toLowerCase() + ".solver"))
				continue;
			
			final ASPInputProgram inputProgram = new ASPInputProgram();
			final DesktopHandler handler = new DesktopHandler(solver.getService(solversDir));
			final LinkedList <Set <Object>> answerSets = new LinkedList <> ();
			final int optionId = handler.addOption(new OptionDescriptor(solver.getOutputOption()));
			
			if(optionFile != null && !optionFile.isEmpty() && !optionFile.equalsIgnoreCase("-no-option-file"))
				FileManager.readFilters(optionFile, solver.name()).forEach(filter -> handler.addOption(new OptionDescriptor(" " + filter)));
			
			handler.addProgram(inputProgram);
			inputProgram.addFilesPath(inputFile);
			
			final LinkedList <String> sortedOutput = OutputManager.processRawOutput(solver.name(), ((AnswerSets)(handler.startSync())).getAnswerSetsString());
			
			handler.removeOption(optionId);
			
			final long start = System.nanoTime();
			
			((AnswerSets)(handler.startSync())).getAnswersets().forEach(answerSet -> {
				try {
					answerSets.add(answerSet.getAtoms());
				} catch(final IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException | NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
					System.exit(-1);
				}
			});
			
			final long end = System.nanoTime();
			
			FileManager.writeToFile(csvFile, solver.name() + ":" + ((Long)((end - start) / 1000000)).toString(), null, true);
			
			if(!answerSets.isEmpty())
				answerSets.forEach(answerSet -> {
					final String tmp = String.join(" ", sortFacts(answerSet));

					if(!sortedOutput.contains(tmp))
						System.out.println("ERROR! Original " + solver.name() + " output does not contain:\n" + tmp + "\n");
				});
		}
		
		FileManager.clearDir(FileManager.CLASSES_PATH);
		System.out.println("END");
	}
}