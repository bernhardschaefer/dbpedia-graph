package de.unima.dws.dbpediagraph.graphdb.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.unima.dws.dbpediagraph.graphdb.disambiguate.ConnectivityMeasure;

/**
 * Basic File Utilities.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class FileUtils {

	/**
	 * Extract a collection of all files from a list of arguments. For each argument, it is checked whether the argument
	 * is a directory or a file. For directories, all files in the directory are returned. Simple file arguments are
	 * added as is.
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

	/**
	 * Parse all non-empty and non-comment lines (comment lines start with '#') from a test results file into a map.
	 * 
	 * @param clazz
	 *            the class whose classpath will be used.
	 */
	public static Map<String, Map<ConnectivityMeasure, Double>> parseDisambiguationResults(String fileName,
			Class<?> clazz) throws IOException, URISyntaxException {

		Map<String, Map<ConnectivityMeasure, Double>> results = new HashMap<>();

		List<String> lines = FileUtils.readLinesFromFile(clazz, fileName);
		if (lines.isEmpty())
			throw new RuntimeException(fileName + "file shouldnt be empty.");

		// multiple tabs as delimiters
		String delimiterRegex = "\t+";
		String[] headers = lines.remove(0).split(delimiterRegex);

		for (String line : lines) {
			String[] values = line.split(delimiterRegex);
			String uri = values[0];

			Map<ConnectivityMeasure, Double> map = new EnumMap<>(ConnectivityMeasure.class);

			for (int i = 1; i < values.length; i++) {
				Double value = Double.parseDouble(values[i]);
				ConnectivityMeasure measure = ConnectivityMeasure.valueOf(headers[i]);
				map.put(measure, value);
			}

			results.put(uri, map);
		}
		return results;
	}

	/**
	 * Read all lines from a file and return all non-empty and non-comment lines (comment lines start with '#').
	 * 
	 * @param clazz
	 *            the class whose classpath will be used.
	 */
	public static List<String> readLinesFromFile(Class<?> clazz, String fileName) throws IOException,
			URISyntaxException {
		URI uri = clazz.getResource(fileName).toURI();
		List<String> lines = Files.readAllLines(Paths.get(uri), StandardCharsets.UTF_8);

		Iterator<String> iter = lines.iterator();
		while (iter.hasNext()) {
			String line = iter.next();
			if (line.isEmpty() || line.startsWith("#"))
				iter.remove();
		}
		return lines;
	}

}
