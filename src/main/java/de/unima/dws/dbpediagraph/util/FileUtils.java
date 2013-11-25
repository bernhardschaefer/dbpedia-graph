package de.unima.dws.dbpediagraph.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.LineProcessor;
import com.google.common.io.Resources;
import com.tinkerpop.blueprints.*;

import de.unima.dws.dbpediagraph.graph.GraphConfig;
import de.unima.dws.dbpediagraph.graph.GraphFactory;
import de.unima.dws.dbpediagraph.model.*;

/**
 * Basic File Utilities.
 * 
 * @author Bernhard Schäfer
 * 
 */
public final class FileUtils {
	public static final String DELIMITER = "\\s+";

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
			if (f.isDirectory())
				files.addAll(Arrays.asList(f.listFiles()));
			else
				files.add(f);
		}
		return files;
	}

	public static String lineToLabel(String[] column) {
		return column.length > 2 ? column[2] : StringUtils.join(column);
	}

	/**
	 * Parse all non-empty and non-comment lines (comment lines start with '#') from a test results file into a map.
	 * 
	 * @param clazz
	 *            the class whose classpath will be used.
	 * @throws ClassNotFoundException
	 */
	public static Map<String, Map<Class<?>, Double>> parseDisambiguationResults(String fileName, Class<?> clazz,
			String packageNameDisambiguator) throws IOException, URISyntaxException, ClassNotFoundException {

		Map<String, Map<Class<?>, Double>> results = new HashMap<>();

		List<String> lines = FileUtils.readNonEmptyNonCommentLinesFromFile(clazz, fileName);
		if (lines.isEmpty())
			throw new IllegalStateException(fileName + "file shouldnt be empty.");

		String[] headers = lines.get(0).split(DELIMITER);

		for (String line : lines.subList(1, lines.size())) {
			String[] values = line.split(DELIMITER);
			String uri = values[0];

			Map<Class<?>, Double> map = new HashMap<>();

			for (int i = 1; i < values.length; i++) {
				Double value = Double.parseDouble(values[i]);
				String fullClassName = packageNameDisambiguator + "." + headers[i];
				Class<?> keyClazz = Class.forName(fullClassName);
				map.put(keyClazz, value);
			}

			results.put(uri, map);
		}
		return results;
	}

	/**
	 * Parse an in-memory graph from text files.
	 */
	public static Graph parseGraph(String verticesFileName, String edgesFileName, Class<?> clazz) throws IOException,
			URISyntaxException {
		Graph graph = GraphFactory.newInMemoryGraph();

		List<String> vertices = FileUtils.readNonEmptyNonCommentLinesFromFile(clazz, verticesFileName);
		List<String> edges = FileUtils.readNonEmptyNonCommentLinesFromFile(clazz, edgesFileName);

		for (String v : vertices) {
			Vertex vertex = graph.addVertex(v);
			vertex.setProperty(GraphConfig.URI_PROPERTY, v);
		}

		for (String line : edges) {
			String[] columns = line.split(DELIMITER);
			Vertex outVertex = graph.getVertex(columns[0]);
			Vertex inVertex = graph.getVertex(columns[1]);
			String label = lineToLabel(columns);
			Edge e = graph.addEdge(label, outVertex, inVertex, label);
			e.setProperty(GraphConfig.URI_PROPERTY, label);
		}
		return graph;
	}

	private static class NonEmptyNonCommentLinesCollector implements LineProcessor<List<String>> {
		final ImmutableList.Builder<String> builder = ImmutableList.builder();
		private static String PREFIX = "#";

		@Override
		public boolean processLine(String line) {
			if (!line.trim().isEmpty() && !line.startsWith(PREFIX))
				builder.add(line);
			return true;
		}

		@Override
		public ImmutableList<String> getResult() {
			return builder.build();
		}
	};

	/**
	 * Read all lines from a file and return all non-empty and non-comment lines (comment lines start with '#').
	 * 
	 * @param clazz
	 *            the class whose classpath will be used.
	 */
	public static List<String> readNonEmptyNonCommentLinesFromFile(Class<?> clazz, String fileName) throws IOException,
			URISyntaxException {
		URL resource = clazz.getResource(fileName);
		if (resource == null)
			throw new IllegalArgumentException("File cannot be found: " + fileName);
		List<String> lines = Resources.readLines(resource, Charsets.UTF_8, new NonEmptyNonCommentLinesCollector());
		return lines;
	}

	public static <T extends SurfaceForm, U extends Sense> Map<T, List<U>> parseSurfaceFormSensesFromFile(String fileName, Class<?> clazz, String uriPrefix, ModelFactory<T, U> factory)
			throws IOException, URISyntaxException {
		Map<T, List<U>> wordsSenses = new HashMap<>();
		List<String> lines = readNonEmptyNonCommentLinesFromFile(clazz, fileName);
		for (String line : lines) {
			List<String> wordSenses = Arrays.asList(line.split(DELIMITER));
			// the first entry is the surface form name, the rest are the senses
			T sf = factory.newSurfaceForm(wordSenses.get(0));
			List<U> senses = sensesFromNotPrefixedUris(wordSenses.subList(1, wordSenses.size()),uriPrefix, factory);
			wordsSenses.put(sf, senses);
		}
		return wordsSenses;
	}

	private static <T extends SurfaceForm, U extends Sense> List<U> sensesFromNotPrefixedUris(List<String> notPrefixedUris, String uriPrefix,
			ModelFactory<T, U> factory) {
		List<U> senses = new ArrayList<>();
		for (String notPrefixedUri: notPrefixedUris)
			senses.add(factory.newSense(uriPrefix + notPrefixedUri));
		return senses;
	}
	

	// Suppress default constructor for noninstantiability
	private FileUtils() {
		throw new AssertionError();
	}

}
