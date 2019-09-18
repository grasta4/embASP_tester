package main;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

final class FileManager {
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
			}
	}
	
	static final void writeToFile(final String file, final String string, final Boolean newLineBegin, final boolean semicolon) {
		String tmp = string + (semicolon ? ';' : "");
		
		if(newLineBegin != null)
			tmp = newLineBegin ? '\n' + string + (semicolon ? ';' : "") : string + (semicolon ? ";\n" : '\n');
		
		try {
			Files.write(Paths.get(file), tmp.getBytes(), StandardOpenOption.APPEND);
		}catch(final IOException e) {
		    e.printStackTrace();
		}
	}
}
