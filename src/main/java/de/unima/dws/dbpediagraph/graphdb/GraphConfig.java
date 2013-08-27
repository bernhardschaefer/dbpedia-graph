package de.unima.dws.dbpediagraph.graphdb;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.tinkerpop.blueprints.Graph;

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

	public static final String URI_PROPERTY = "URI";
	public static final String DBPEDIA_RESOURCE_PREFIX = "http://dbpedia.org/resource/";
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

	private GraphConfig() {
		try {
			config = new PropertiesConfiguration(GRAPH_PROPERTY_FILE);
		} catch (ConfigurationException e) {
			throw new IllegalArgumentException(GRAPH_PROPERTY_FILE + " could not be loaded.", e);
		}
	}

	public Configuration getConfig() {
		return config;
	}

}
