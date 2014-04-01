package de.unima.dws.dbpediagraph.graph;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.apache.commons.configuration.*;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Graph;

/**
 * Loads and observes the central configuration file. Uses a redirect mechanism described in redirect.properties.
 * 
 * @author Bernhard Schäfer
 */
public final class GraphConfig {
	private static final Logger logger = LoggerFactory.getLogger(GraphConfig.class);

	public static final String URI_PROPERTY = "URI";

	public static final String EDGE_LABEL = "pred";

	private static final String GRAPHDB_REDIRECT_PROPERTY_FILE = "redirect.properties";
	private static final String CONFIG_RELOADING_FILE = "file";
	private static final String CONFIG_GRAPH_DIRECTORY = "graph.directory";

	/**
	 * The config file that is used for retrieving {@link Graph} implementations.
	 */
	private static Configuration config;
	static {
		String fileName = getRedirectedConfigFileName();
		try {
			PropertiesConfiguration reloadingConfig = new PropertiesConfiguration(fileName);
			reloadingConfig.setReloadingStrategy(new FileChangedReloadingStrategy());
			config = reloadingConfig;
		} catch (ConfigurationException e) {
			throw new IllegalArgumentException(fileName + " could not be loaded.", e);
		}
		logger.info("Using reloading strategy for properties file {}", fileName);
	}

	public static Configuration config() {
		return config;
	}

	private static String getRedirectedConfigFileName() {
		String fileName = null;
		try {
			PropertiesConfiguration redirectConfig = new PropertiesConfiguration(GRAPHDB_REDIRECT_PROPERTY_FILE);
			redirectConfig.setThrowExceptionOnMissing(true);
			fileName = redirectConfig.getString(CONFIG_RELOADING_FILE);
		} catch (ConfigurationException e) {
			throw new IllegalStateException("There is a redirect property file but it could not be loaded", e);
		}

		checkNotNull(fileName); // this shouldn't happen
		checkState(ConfigurationUtils.locate(fileName) != null,
				String.format("The provided file name %s is not valid", fileName));

		return fileName;
	}

	public static String graphDirectory() {
		return config.getString(CONFIG_GRAPH_DIRECTORY);
	}

	// Suppress default constructor for non-instantiability
	private GraphConfig() {
	}

}
