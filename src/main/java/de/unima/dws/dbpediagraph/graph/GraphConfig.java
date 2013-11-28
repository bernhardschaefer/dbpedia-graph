package de.unima.dws.dbpediagraph.graph;

import org.apache.commons.configuration.*;

import com.tinkerpop.blueprints.Graph;

/**
 * The configuration hub for the DBpedia graph project.
 * 
 * @author Bernhard Schäfer
 * 
 */
public final class GraphConfig {
	public static final String URI_PROPERTY = "URI";

	public static final String DBPEDIA_RESOURCE_PREFIX = "http://dbpedia.org/resource/";
	public static final String EDGE_LABEL = "pred";

	private static final String GRAPHDB_PROPERTY_FILE = "graphdb.properties";

	private static final String CONFIG_GRAPH_DIRECTORY = "graph.directory";

	/**
	 * The config file that is used for retrieving {@link Graph} implementations.
	 */
	private static Configuration config;
	static {
		try {
			config = new PropertiesConfiguration(GRAPHDB_PROPERTY_FILE);
		} catch (ConfigurationException e) {
			throw new IllegalArgumentException(GRAPHDB_PROPERTY_FILE + " could not be loaded.", e);
		}
	}

	public static Configuration config() {
		return config;
	}

	public static String graphDirectory() {
		return config.getString(CONFIG_GRAPH_DIRECTORY);
	}

	// Suppress default constructor for non-instantiability
	private GraphConfig() {
	}

}
