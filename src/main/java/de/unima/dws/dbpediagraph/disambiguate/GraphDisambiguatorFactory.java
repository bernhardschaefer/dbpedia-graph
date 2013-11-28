package de.unima.dws.dbpediagraph.disambiguate;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.configuration.Configuration;

import de.unima.dws.dbpediagraph.graph.GraphType;
import de.unima.dws.dbpediagraph.model.Sense;
import de.unima.dws.dbpediagraph.model.SurfaceForm;
import de.unima.dws.dbpediagraph.subgraph.SubgraphConstructionSettings;
import de.unima.dws.dbpediagraph.weights.EdgeWeights;

/**
 * Factory for retrieving {@link GraphDisambiguatorFactory} implementations from {@link Configuration}.
 * 
 * @author Bernhard Schäfer
 * 
 */
public final class GraphDisambiguatorFactory {

	private static final String CONFIG_LOCAL_DISAMBIGUATOR = "local.graph.disambiguator";
	private static final String CONFIG_GLOBAL_DISAMBIGUATOR = "global.graph.disambiguator";;

	@SuppressWarnings("unchecked")
	public static <T extends SurfaceForm, U extends Sense> GlobalGraphDisambiguator<T, U> newGlobalFromConfig(
			Configuration config, SubgraphConstructionSettings subgraphConstructionSettings, EdgeWeights graphWeights) {
		String disambiguatorClassName = config.getString(CONFIG_GLOBAL_DISAMBIGUATOR);
		try {
			@SuppressWarnings("rawtypes")
			Class<? extends GlobalGraphDisambiguator> globalDisambiguatorClass = Class.forName(disambiguatorClassName)
					.asSubclass(GlobalGraphDisambiguator.class);
			return globalDisambiguatorClass.getConstructor(SubgraphConstructionSettings.class, EdgeWeights.class)
					.newInstance(subgraphConstructionSettings, graphWeights);
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(
					String.format(
							"Error while trying to create global disambiguator instance. Check if provided global disambiguator class %s is valid.",
							disambiguatorClassName), e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends SurfaceForm, U extends Sense> LocalGraphDisambiguator<T, U> newLocalFromConfig(
			Configuration config, GraphType graphType, EdgeWeights graphWeights) {
		String disambiguatorClassName = config.getString(CONFIG_LOCAL_DISAMBIGUATOR);
		try {
			@SuppressWarnings("rawtypes")
			Class<? extends LocalGraphDisambiguator> localDisambiguatorClass = Class.forName(disambiguatorClassName)
					.asSubclass(LocalGraphDisambiguator.class);
			return localDisambiguatorClass.getConstructor(GraphType.class, EdgeWeights.class).newInstance(graphType,
					graphWeights);
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(
					String.format(
							"Error while trying to create local disambiguator instance. Check if provided local disambiguator class %s is valid.",
							disambiguatorClassName), e);
		}
	}

	// Suppress default constructor for non-instantiability
	private GraphDisambiguatorFactory() {
	}
}
