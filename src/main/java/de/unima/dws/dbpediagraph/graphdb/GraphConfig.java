package de.unima.dws.dbpediagraph.graphdb;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.GraphFactory;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.util.wrappers.batch.BatchGraph;

/**
 * The configuration hub for the DBpedia graph project.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class GraphConfig {
	/**
	 * Holder for the singleton.
	 */
	private static class Holder {
		public static final GraphConfig INSTANCE = new GraphConfig();
	}

	private static final Logger logger = LoggerFactory.getLogger(GraphConfig.class);

	public static final String URI_PROPERTY = "URI";
	public static final String DBPEDIA_PREFIX = "http://dbpedia.org/";
	public static final String EDGE_LABEL = "pred";
	private static final String GRAPH_PROPERTY_FILE = "graph.properties";

	public static GraphConfig getInstance() {
		return Holder.INSTANCE;
	}

	/**
	 * The config file that is used for retrieving {@link Graph}
	 * implementations.
	 */
	private Configuration config;

	private final TransactionalGraph graph;

	private GraphConfig() {
		try {
			config = new PropertiesConfiguration(GRAPH_PROPERTY_FILE);
		} catch (ConfigurationException e) {
			throw new IllegalArgumentException(GRAPH_PROPERTY_FILE + " could not be loaded.", e);
		}

		graph = openGraph();

	}

	/**
	 * Creates and returns a graph implementation. The graph is created for
	 * batch inserts using the provided buffer size.
	 */
	public Graph getBatchGraph(long bufferSize) {
		return new BatchGraph<TransactionalGraph>(graph, bufferSize);
	}

	public TransactionalGraph getGraph() {
		return graph;
	}

	/**
	 * Open a graph based on configuration settings.
	 */
	private TransactionalGraph openGraph() {
		long startTime = System.currentTimeMillis();

		Graph graph = GraphFactory.open(config);

		logger.debug(String.format("Graph loading time %.2f sec %n", (System.currentTimeMillis() - startTime) / 1000.0));
		if (graph instanceof TransactionalGraph) {
			return (TransactionalGraph) graph;
		} else {
			throw new IllegalArgumentException("Graph specified in properties needs to be a transactional graph.");
		}
	}
}
