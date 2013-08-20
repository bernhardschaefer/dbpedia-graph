package de.unima.dws.dbpediagraph.graphdb.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.configuration.ConfigurationException;
import org.openrdf.rio.ParseErrorListener;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Graph;

import de.unima.dws.dbpediagraph.graphdb.GraphConfig;
import de.unima.dws.dbpediagraph.graphdb.util.FileUtils;
import de.unima.dws.dbpediagraph.graphdb.util.LoadingMetrics;

/**
 * Main class for importing DBpedia into a {@link Graph}.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class DBpediaGraphLoader {

	/**
	 * The buffer size for batch importing. Every time the buffer fills up its
	 * vertices and edges are persisted to the graph.
	 */
	private static final long BUFFER_SIZE = 100_000;

	private static final Logger logger = LoggerFactory.getLogger(DBpediaGraphLoader.class);

	/**
	 * Main method to be called for importing DBpedia into a {@link Graph}.
	 * 
	 * @param args
	 *            each arg can be a directory containing RDF files or a RDF file
	 *            itself.
	 */
	public static void main(String[] args) throws ConfigurationException {
		new DBpediaGraphLoader().loadFromFiles(FileUtils.extractFilesFromArgs(args));
	}

	/**
	 * Generates a {@link Graph} by parsing the provided RDF files.
	 */
	public void loadFromFiles(Collection<File> files) throws ConfigurationException {
		if (files == null || files.size() == 0) {
			throw new IllegalArgumentException("Provide one or several directories or files in RDF Format.");
		}

		// track loading metrics
		LoadingMetrics globalMetric = new LoadingMetrics("OVERALL");
		List<LoadingMetrics> metrics = new LinkedList<LoadingMetrics>();

		Graph graph = GraphConfig.getInstance().getBatchGraph(BUFFER_SIZE);

		for (File f : files) {
			LoadingMetrics metric = new LoadingMetrics(f.getName());

			// get appropriate handler
			RDFHandlerVerbose handler = new BatchHandler(graph);
			for (Entry<String, String> prefix : GraphConfig.URI_TO_PREFIX.entrySet()) {
				try {
					handler.handleNamespace(prefix.getValue(), prefix.getKey());
				} catch (RDFHandlerException e) {
					logger.error("Exception while trying to assign prefixes", e);
				}
			}

			// get appropriate parser
			RDFFormat format = RDFFormat.forFileName(f.getName());
			if (format == null) {
				logger.warn(String.format("File %s could not be parsed since it has no valid RDF format file ending.",
						f.getName()));
				continue;
			}
			RDFParser rdfParser = Rio.createParser(format);
			rdfParser.setStopAtFirstError(false);
			rdfParser.setParseErrorListener(new ParseErrorListener() {
				@Override
				public void error(String msg, int lineNo, int colNo) {
					logger.warn(String.format("Error %s at line %d and colNo %d%n", msg, lineNo, colNo));
				}

				@Override
				public void fatalError(String msg, int lineNo, int colNo) {
					logger.warn(String.format("Fatal Error %s at line %d and colNo %d%n", msg, lineNo, colNo));
				}

				@Override
				public void warning(String msg, int lineNo, int colNo) {
					logger.debug(String.format("Warning %s at line %d and colNo %d%n", msg, lineNo, colNo));
				}
			});

			// assign handler to parser
			rdfParser.setRDFHandler(handler);

			// parse file
			String baseURI = "";
			try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
				rdfParser.parse(reader, baseURI);
			} catch (RDFParseException | RDFHandlerException | IOException e) {
				logger.error("Error while parsing file " + f.getName(), e);
			}

			// log metrics
			metric.finish(handler);
			metrics.add(metric);
		}

		graph.shutdown();

		globalMetric.finish(metrics);
	}
}
