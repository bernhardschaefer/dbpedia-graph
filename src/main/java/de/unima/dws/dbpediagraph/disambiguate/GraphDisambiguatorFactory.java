package de.unima.dws.dbpediagraph.disambiguate;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private static final Logger logger = LoggerFactory.getLogger(GraphDisambiguatorFactory.class);

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

		PriorStrategy priorStrategy = PriorStrategy.fromConfig(config);
		if (priorStrategy != null) {
			double threshold = PriorStrategy.getThresholdFromConfig(config);
			logger.debug("Using PriorStrategy {} with threshold {}", priorStrategy, threshold);
			return new PriorStrategyDisambiguatorDecorator<>(disambiguator, priorStrategy, threshold);
		} else
			return disambiguator;
	}

	// Suppress default constructor for non-instantiability
	private GraphDisambiguatorFactory() {
	}
}
