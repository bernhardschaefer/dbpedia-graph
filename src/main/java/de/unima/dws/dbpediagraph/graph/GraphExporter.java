package de.unima.dws.dbpediagraph.graph;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLWriter;

/**
 * Utilities for serializing {@link Graph}s.
 * 
 * @author Bernhard Schäfer
 * 
 */
public final class GraphExporter {
	private static final Logger logger = LoggerFactory.getLogger(GraphExporter.class);
	private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss.SSS");

	public static void persistGraph(Graph graph, boolean normalize, String fileName) {
		checkState(checkNotNull(fileName).length() > 0);
		GraphMLWriter writer = new GraphMLWriter(graph);
		writer.setNormalize(normalize);
		try {
			Files.createParentDirs(new File(fileName));
			writer.outputGraph(fileName);
		} catch (IOException e) {
			logger.error("Error while trying to persist subgraph.", e);
		}
	}

	public static void persistGraphInDirectory(Graph graph, boolean normalize, String dirName) {
		String fileName = dirName + File.separator + "subgraph" + DATE_FORMATTER.format(new Date() + ".xml");
		persistGraph(graph, normalize, fileName);
	}

	// suppress default constructor for non-instantiability
	private GraphExporter() {
	}
}
