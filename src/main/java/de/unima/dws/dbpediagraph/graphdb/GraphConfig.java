package de.unima.dws.dbpediagraph.graphdb;

import java.util.LinkedHashMap;
import java.util.Map;

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
	public static final String DBPEDIA_RESOURCE_URI = "http://dbpedia.org/resource/";
	public static final String EDGE_LABEL = "pred";
	private static final String GRAPH_PROPERTY_FILE = "graph.properties";

	public static final Map<String, String> URI_TO_PREFIX;
	static {
		URI_TO_PREFIX = new LinkedHashMap<String, String>();

		// subject and objects are all dbpedia resources
		URI_TO_PREFIX.put(DBPEDIA_RESOURCE_URI, "dbr:");

		// top predicates extracted from dumps
		URI_TO_PREFIX.put("http://dbpedia.org/ontology/", "dbo:");
		URI_TO_PREFIX.put("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf:");
		URI_TO_PREFIX.put("http://purl.org/dc/terms/subject", "dcterms:");
		URI_TO_PREFIX.put("http://xmlns.com/foaf/0.1/", "foaf:");
		URI_TO_PREFIX.put("http://www.w3.org/2004/02/skos/core#", "skos:");
		URI_TO_PREFIX.put("http://www.w3.org/2003/01/geo/wgs84_pos#", "pos:");
		URI_TO_PREFIX.put("http://purl.org/dc/elements/1.1/", "dc:");
		URI_TO_PREFIX.put("http://www.georss.org/georss/point", "poi:");

		// other prefixes from http://dbpedia.org/snorql/
		URI_TO_PREFIX.put("http://www.w3.org/2002/07/owl#", "owl:");
		URI_TO_PREFIX.put("http://www.w3.org/2001/XMLSchema#", "xsd:");
		URI_TO_PREFIX.put("http://www.w3.org/2000/01/rdf-schema#", "rdfs:");
		URI_TO_PREFIX.put("http://dbpedia.org/property/", "dbpedia2:");
	}

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

		logger.debug(String.format("Graph loading time %.2f sec", (System.currentTimeMillis() - startTime) / 1000.0));
		if (graph instanceof TransactionalGraph) {
			return (TransactionalGraph) graph;
		} else {
			throw new IllegalArgumentException("Graph specified in properties needs to be a transactional graph.");
		}
	}
}
