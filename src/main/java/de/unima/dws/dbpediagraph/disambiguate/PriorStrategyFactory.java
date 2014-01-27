package de.unima.dws.dbpediagraph.disambiguate;

import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unima.dws.dbpediagraph.model.*;
import de.unima.dws.dbpediagraph.util.EnumUtils;

/**
 * Factory for retrieving {@link PriorStrategy} implementations.
 * 
 * @author Bernhard Schäfer
 * 
 */
class PriorStrategyFactory {
	private static final Logger logger = LoggerFactory.getLogger(PriorStrategyFactory.class);

	private static final String CONFIG_PRIOR_STRATEGY = "de.unima.dws.dbpediagraph.disambiguate.priorstrategy";
	private static final String CONFIG_PRIOR_STRATEGY_THRESHOLD = "de.unima.dws.dbpediagraph.disambiguate.priorstrategy.threshold";

	public enum PriorStrategyType {
		NO_ANNOTATION, SINGLETON_FALLBACK, CONFIDENCE_FALLBACK;
	}

	public static PriorStrategy fromConfig(Configuration config) {
		PriorStrategyType priorStrategyType = EnumUtils.fromConfig(PriorStrategyType.class, config,
				CONFIG_PRIOR_STRATEGY);
		double threshold = config.getDouble(CONFIG_PRIOR_STRATEGY_THRESHOLD);
		return fromPriorStrategyType(priorStrategyType, threshold);
	}

	public static PriorStrategy fromPriorStrategyType(PriorStrategyType priorStrategyType, double threshold) {
		switch (priorStrategyType) {
		case CONFIDENCE_FALLBACK:
			return new ConfidenceFallbackPriorStrategy(threshold);
		case NO_ANNOTATION:
			return new NoAnnotationPriorStrategy(threshold);
		case SINGLETON_FALLBACK:
			return new SingletonFallbackPriorStrategy(threshold);
		}
		throw new IllegalArgumentException("The specified prior strategy type is not valid: " + priorStrategyType);
	}

	/**
	 * Set priors as scores if available for all surface form sense scores
	 */
	static void assignPriors(SurfaceForm surfaceForm,
			List<? extends SurfaceFormSenseScore<? extends SurfaceForm, ? extends Sense>> sfss) {
		for (SurfaceFormSenseScore<?, ?> surfaceFormSenseScore : sfss) {
			Double prior = surfaceFormSenseScore.getSense().prior();
			if (prior != null)
				surfaceFormSenseScore.setScore(prior);
		}
	}

	/**
	 * Log the strategy that has been used in prior strategy implementations. Can be used with e.g. grep -c on logfile
	 * to gather usage statistics.
	 */
	public static void logUsedStrategy(double confidence, double threshold, PriorStrategy strategy) {
		if (confidence <= threshold)
			logger.debug("Confidence {} smaller than threshold {}. Use prior strategy with class {}", confidence,
					threshold, strategy.getClass().getSimpleName());
		else
			logger.debug("Confidence {} equal or larger than threshold {}. No prior strategy with class {}",
					confidence, threshold, strategy.getClass().getSimpleName());

	}

}
