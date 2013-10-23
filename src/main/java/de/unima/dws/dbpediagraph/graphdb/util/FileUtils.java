package de.unima.dws.dbpediagraph.graphdb.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dbpedia.spotlight.model.DBpediaResource;
import org.dbpedia.spotlight.model.SurfaceForm;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.GraphConfig;
import de.unima.dws.dbpediagraph.graphdb.GraphFactory;
import de.unima.dws.dbpediagraph.graphdb.Graphs;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.SurfaceFormSenses;

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

	public static Collection<Collection<Vertex>> parseAllWordsSenses(Graph graph, String fileName, Class<?> clazz,
			String uriPrefix) throws IOException, URISyntaxException {
		Collection<Collection<String>> wordsSensesString = readUrisFromFile(clazz, fileName, uriPrefix);
		return Graphs.wordsVerticesByUri(graph, wordsSensesString);
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

		List<String> lines = FileUtils.readRelevantLinesFromFile(clazz, fileName);
		if (lines.isEmpty())
			throw new RuntimeException(fileName + "file shouldnt be empty.");

		String[] headers = lines.remove(0).split(DELIMITER);

		for (String line : lines) {
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

		List<String> vertices = FileUtils.readRelevantLinesFromFile(clazz, verticesFileName);
		List<String> edges = FileUtils.readRelevantLinesFromFile(clazz, edgesFileName);

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

	/**
	 * Read all lines from a file and return all non-empty and non-comment lines (comment lines start with '#').
	 * 
	 * @param clazz
	 *            the class whose classpath will be used.
	 */
	public static List<String> readRelevantLinesFromFile(Class<?> clazz, String fileName) throws IOException,
			URISyntaxException {
		URL url = clazz.getResource(fileName);
		if (url == null)
			throw new IllegalArgumentException("File cannot be found: " + fileName);
		List<String> lines = Files.readAllLines(Paths.get(url.toURI()), StandardCharsets.UTF_8);

		Iterator<String> iter = lines.iterator();
		while (iter.hasNext()) {
			String line = iter.next();
			if (line.isEmpty() || line.startsWith("#"))
				iter.remove();
		}
		return lines;
	}

	public static Collection<SurfaceFormSenses> surfaceFormsSensesFromFile(Class<?> clazz, String fileName, String uriPrefix)
			throws IOException, URISyntaxException {
		Collection<SurfaceFormSenses> wordsSenses = new ArrayList<>();
		List<String> lines = readRelevantLinesFromFile(clazz, fileName);
		for (String line : lines)
			wordsSenses.add(surfaceFormSensesFromLine(line, uriPrefix));
		return wordsSenses;
	}

	public static SurfaceFormSenses surfaceFormSensesFromLine(String line, String uriPrefix) {
		Collection<DBpediaResource> senses = new ArrayList<>();
		String[] wordSenses = line.split(DELIMITER);
		for (int i = 0; i < wordSenses.length; i++) {
			String uri = uriPrefix + wordSenses[i];
			senses.add(new DBpediaResource(uri));
		}
		return new SurfaceFormSenses(new SurfaceForm("unknown"), senses);
	}

	public static Collection<Collection<String>> readUrisFromFile(Class<?> clazz, String fileName, String uriPrefix)
			throws IOException, URISyntaxException {
		Collection<Collection<String>> wordsSenses = new ArrayList<>();
		List<String> lines = readRelevantLinesFromFile(clazz, fileName);
		for (String line : lines) {
			String[] wordSenses = line.split(DELIMITER);
			for (int i = 0; i < wordSenses.length; i++)
				wordSenses[i] = uriPrefix + wordSenses[i];
			wordsSenses.add(Arrays.asList(wordSenses));
		}
		return wordsSenses;
	}

	// Suppress default constructor for noninstantiability
	private FileUtils() {
		throw new AssertionError();
	}

}
