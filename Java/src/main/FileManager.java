package main;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

final class FileManager {
	static final String CLASSES_PATH = "_classes";
	
	private FileManager() {
		
	}
	
	static final void clearDir(final String dirPath) {
		final Path path = Paths.get(dirPath);
		
		if(Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS))
			try(final DirectoryStream <Path> entries = Files.newDirectoryStream(path)) {
				for(Path entry : entries)
					Files.delete(entry);
			} catch (final IOException e) {
				e.printStackTrace();
				System.exit(-1);
			}
	}
	
	static final boolean solverPresent(final String solversDir, final String solverName) {
		try(Stream<Path> paths = Files.walk(Paths.get(solversDir))) {
		    for(final Object path : paths.filter(Files::isRegularFile).toArray())
		    	if(((Path)path).endsWith(solverName))
		    		return true;
		} catch(final IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		return false;
	}
	
	static final List <String> readFilters(final String fileName, final String matchSolver) {
		final LinkedList <String> filters = new LinkedList <> ();
		
		try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
	        stream.forEach(filter -> {
	        	if(filter.startsWith(matchSolver + ":"))
	        		filters.add(filter.substring(filter.indexOf(':') + 1).trim());
	        });
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	
		return filters;
	}
	
	static final void writeToFile(final String file, final String string, final Boolean newLineBegin, final boolean semicolon) {
		String tmp = string + (semicolon ? ';' : "");
		
		if(newLineBegin != null)
			tmp = newLineBegin ? '\n' + string + (semicolon ? ';' : "") : string + (semicolon ? ";\n" : '\n');
		
		try {
			Files.write(Paths.get(file), tmp.getBytes(), StandardOpenOption.APPEND);
		}catch(final IOException e) {
		    e.printStackTrace();
		    System.exit(-1);
		}
	}
}
