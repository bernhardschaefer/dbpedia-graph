package de.unima.dws.dbpediagraph.disambiguate;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.configuration.Configuration;

import de.unima.dws.dbpediagraph.graph.GraphType;
import de.unima.dws.dbpediagraph.model.Sense;
import de.unima.dws.dbpediagraph.model.SurfaceForm;
import de.unima.dws.dbpediagraph.weights.EdgeWeights;
import de.unima.dws.dbpediagraph.weights.EdgeWeightsFactory;

/**
 * Factory for retrieving {@link GraphDisambiguator} implementations from {@link Configuration}.
 * 
 * @author Bernhard Schäfer
 * 
 */
public final class GraphDisambiguatorFactory {

	private static final String CONFIG_DISAMBIGUATOR = "de.unima.dws.dbpediagraph.graph.disambiguator";

	@SuppressWarnings("unchecked")
	public static <T extends SurfaceForm, U extends Sense> GraphDisambiguator<T, U> newFromConfig(Configuration config) {
		EdgeWeights edgeWeights = EdgeWeightsFactory.dbpediaFromConfig(config);
		GraphType graphType = GraphType.fromConfig(config);

		GraphDisambiguator<T, U> disambiguator;
		String disambiguatorClassName = config.getString(CONFIG_DISAMBIGUATOR);
		try {
			@SuppressWarnings("rawtypes")
			Class<? extends GraphDisambiguator> disambiguatorClass = Class.forName(disambiguatorClassName).asSubclass(
					GraphDisambiguator.class);
			disambiguator = disambiguatorClass.getConstructor(GraphType.class, EdgeWeights.class).newInstance(
					graphType, edgeWeights);
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(
					String.format(
							"Error while trying to create disambiguator instance. Check if provided disambiguator class %s is valid.",
							disambiguatorClassName), e);
		}

		PriorStrategy priorStrategy = PriorStrategyFactory.fromConfig(config);
		return new PriorStrategyDisambiguatorDecorator<>(disambiguator, priorStrategy);
	}

	// Suppress default constructor for non-instantiability
	private GraphDisambiguatorFactory() {
	}
}
