package de.unima.dws.dbpediagraph.disambiguate;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.configuration.Configuration;

import de.unima.dws.dbpediagraph.graph.GraphType;
import de.unima.dws.dbpediagraph.model.Sense;
import de.unima.dws.dbpediagraph.model.SurfaceForm;
import de.unima.dws.dbpediagraph.weights.EdgeWeights;
import de.unima.dws.dbpediagraph.weights.EdgeWeightsFactory;

/**
 * Factory for retrieving {@link GraphDisambiguatorFactory} implementations from {@link Configuration}.
 * 
 * @author Bernhard Schäfer
 * 
 */
public final class GraphDisambiguatorFactory {

	private static final String CONFIG_DISAMBIGUATOR = "de.unima.dws.dbpediagraph.graph.disambiguator";

	/** use prior probability of entities if all candidates are unconnected singletons */
	private static final String CONFIG_PRIOR_FALLBACK = "de.unima.dws.dbpediagraph.graph.disambiguator.singletonprior";

	@SuppressWarnings("unchecked")
	public static <T extends SurfaceForm, U extends Sense> GraphDisambiguator<T, U> newFromConfig(Configuration config) {
		EdgeWeights edgeWeights = EdgeWeightsFactory.dbpediaFromConfig(config);
		GraphType graphType = GraphType.fromConfig(config);
		boolean usePriorFallback = config.getBoolean(CONFIG_PRIOR_FALLBACK);

		String disambiguatorClassName = config.getString(CONFIG_DISAMBIGUATOR);
		try {
			@SuppressWarnings("rawtypes")
			Class<? extends GraphDisambiguator> disambiguatorClass = Class.forName(disambiguatorClassName).asSubclass(
					GraphDisambiguator.class);
			return disambiguatorClass.getConstructor(GraphType.class, EdgeWeights.class, Boolean.class).newInstance(
					graphType, edgeWeights, usePriorFallback);
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(
					String.format(
							"Error while trying to create disambiguator instance. Check if provided disambiguator class %s is valid.",
							disambiguatorClassName), e);
		}
	}

	// Suppress default constructor for non-instantiability
	private GraphDisambiguatorFactory() {
	}
}
