package de.unima.dws.dbpediagraph.graphdb;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.configuration.*;

import com.tinkerpop.blueprints.Graph;

import de.unima.dws.dbpediagraph.graphdb.disambiguate.GlobalGraphDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.LocalGraphDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.global.Compactness;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.local.DegreeCentrality;
import de.unima.dws.dbpediagraph.graphdb.model.*;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstructionSettings;

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

	private static final String GRAPHDB_PROPERTY_FILE = "graphdb.properties";

	private static final String GRAPH_DIRECTORY_KEY = "graph.directory";

	private static final String DEFAULT_LOCAL_DISAMBIGUATOR = DegreeCentrality.class.getName();
	private static final String LOCAL_DISAMBIGUATOR_KEY = "local.graph.disambiguator";

	private static final String DEFAULT_GLOBAL_DISAMBIGUATOR = Compactness.class.getName();
	private static final String GLOBAL_DISAMBIGUATOR_KEY = "global.graph.disambiguator";;

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
		return config.getString(GRAPH_DIRECTORY_KEY);
	}

	@SuppressWarnings("unchecked")
	public static <T extends SurfaceForm, U extends Sense> GlobalGraphDisambiguator<T, U> newGlobalDisambiguator(
			Configuration configuration, SubgraphConstructionSettings subgraphConstructionSettings,
			ModelFactory<T, U> factory) {
		String disambiguatorClassName = config.getString(GLOBAL_DISAMBIGUATOR_KEY, DEFAULT_GLOBAL_DISAMBIGUATOR);
		try {
			@SuppressWarnings("rawtypes")
			Class<? extends GlobalGraphDisambiguator> globalDisambiguatorClass = Class.forName(disambiguatorClassName)
					.asSubclass(GlobalGraphDisambiguator.class);
			return globalDisambiguatorClass.getConstructor(SubgraphConstructionSettings.class, ModelFactory.class)
					.newInstance(subgraphConstructionSettings, factory);
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException("Error while trying to create disambiguator instance.", e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends SurfaceForm, U extends Sense> LocalGraphDisambiguator<T, U> newLocalDisambiguator(
			GraphType graphType, ModelFactory<T, U> factory) {
		// TODO log if no disambiguator in config
		String disambiguatorClassName = config.getString(LOCAL_DISAMBIGUATOR_KEY, DEFAULT_LOCAL_DISAMBIGUATOR);
		try {
			@SuppressWarnings("rawtypes")
			Class<? extends LocalGraphDisambiguator> localDisambiguatorClass = Class.forName(disambiguatorClassName)
					.asSubclass(LocalGraphDisambiguator.class);
			return localDisambiguatorClass.getConstructor(GraphType.class, ModelFactory.class).newInstance(graphType,
					factory);
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException("Error while trying to create disambiguator instance.", e);
		}
	}

	// Suppress default constructor for noninstantiability
	private GraphConfig() {
		throw new AssertionError();
	}

}
