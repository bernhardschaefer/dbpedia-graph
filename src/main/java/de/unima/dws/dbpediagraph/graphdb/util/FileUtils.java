package de.unima.dws.dbpediagraph.graphdb.util;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Basic File Utilities.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class FileUtils {

	/**
	 * Extract a collection of all files from a list of arguments. For each
	 * argument, it is checked whether the argument is a directory or a file.
	 * For directories, all files in the directory are returned. Simple file
	 * arguments are added as is.
	 * 
	 * @param args
	 *            an array of directories and file locations
	 */
	public static Collection<File> extractFilesFromArgs(String[] args) {
		List<File> files = new LinkedList<File>();
		for (String arg : args) {
			File f = new File(arg);
			if (f.isDirectory()) {
				files.addAll(Arrays.asList(f.listFiles()));
			} else {
				files.add(f);
			}
		}
		return files;
	}

}
