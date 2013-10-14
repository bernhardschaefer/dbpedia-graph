package de.unima.dws.dbpediagraph.graphdb;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.tinkerpop.blueprints.Graph;

/**
 * The configuration hub for the DBpedia graph project. The class is noninstantiable and needs to be accessed in a
 * static way.
 * 
 * @author Bernhard Schäfer
 * 
 */
public final class GraphConfig {
	public static final String URI_PROPERTY = "URI";

	public static final String DBPEDIA_RESOURCE_PREFIX = "http://dbpedia.org/resource/";
	public static final String EDGE_LABEL = "pred";

	private static final String GRAPH_PROPERTY_FILE = "graph.properties";

	/**
	 * The config file that is used for retrieving {@link Graph} implementations.
	 */
	private static Configuration config;

	static {
		try {
			config = new PropertiesConfiguration(GRAPH_PROPERTY_FILE);
		} catch (ConfigurationException e) {
			throw new IllegalArgumentException(GRAPH_PROPERTY_FILE + " could not be loaded.", e);
		}
	}

	public static Configuration config() {
		return config;
	}

	// Suppress default constructor for noninstantiability
	private GraphConfig() {
		throw new AssertionError();
	}

}
