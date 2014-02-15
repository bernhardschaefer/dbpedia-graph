package de.unima.dws.dbpediagraph.disambiguate;

import java.util.Collections;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unima.dws.dbpediagraph.model.*;
import de.unima.dws.dbpediagraph.util.EnumUtils;

/**
 * Prior strategy to revise graph-based entity candidate scores based on their scores and prior probability information.
 * 
 * @author Bernhard Schäfer
 * 
 */
// TODO rename; this is not prior strategy but more like graphConfidenceThresholdStrategy since e.g. with NoAnnotation
// priors are not used
enum PriorStrategy {
	/**
	 * Confidence-based prior strategy that assigns prior probabilities to each score of all candidate entities of a
	 * surface form if the confidence for the first candidate falls below a specified threshold.
	 */
	CONFIDENCE_FALLBACK {
		@Override
		<T extends SurfaceForm, U extends Sense> void reviseScores(T surfaceForm,
				List<SurfaceFormSenseScore<T, U>> sfss, double threshold) {
			// only check confidence if we more than one 1 candidate
			// if there is only one candidate we will assume that this candidate is likely correct
			if (sfss.size() > 1) {
				Collections.sort(sfss, SurfaceFormSenseScore.DESCENDING_SCORE_COMPARATOR);
				SurfaceFormSenseScore<T, U> first = sfss.get(0);
				SurfaceFormSenseScore<T, U> second = sfss.get(1);

				// see ConfidenceFilter.scala for further details (esp. PercentageOfSecondRank Filter)
				// // confidence calculation from DBTwoStepDisambiguator.bestK_()
				// double confidence = Math.exp(second.getScore() - first.getScore());
				// if (confidence <= threshold) {
				// PriorStrategyFactory.assignPriors(surfaceForm, sfss);
				// }

				// naive confidence calculation
				double confidence = 1.0 - (second.getScore() / first.getScore());
				if (confidence <= threshold)
					assignPriors(surfaceForm, sfss);

				logUsedStrategy(confidence, threshold, this);
			}
		}
	},
	/**
	 * Deletes all entity candidates of a surface form if their highest score falls below a certain threshold. If the
	 * threshold is defined as zero, this strategy prevents the random selection of a candidate if all candidates are
	 * singletons, meaning they do not have any connections in the subgraph.
	 */
	NO_ANNOTATION {
		@Override
		<T extends SurfaceForm, U extends Sense> void reviseScores(T surfaceForm,
				List<SurfaceFormSenseScore<T, U>> sfss, double threshold) {
			double confidence = sfss.isEmpty() ? 1.0 : Collections.max(sfss,
					SurfaceFormSenseScore.ASCENDING_SCORE_COMPARATOR).getScore();
			if (confidence <= threshold)
				sfss.clear(); // delete all candidates so annotation is done

			logUsedStrategy(confidence, threshold, this);
		}
	},
	/**
	 * Assign the prior probability as the score of all entity candidates of a surface form if all entity candidates are
	 * unconnected, which means they have a score of zero.
	 */
	// TODO change to MinScoreFallbackPriorStrategy; change enum; add explanation that threshold == 0 means
	// SingletonFallback
	SINGLETON_FALLBACK {
		@Override
		<T extends SurfaceForm, U extends Sense> void reviseScores(T surfaceForm,
				List<SurfaceFormSenseScore<T, U>> sfss, double threshold) {
			// check if there are only singletons
			double confidence = sfss.isEmpty() ? 1.0 : Collections.max(sfss,
					SurfaceFormSenseScore.ASCENDING_SCORE_COMPARATOR).getScore();
			if (confidence <= threshold)
				assignPriors(surfaceForm, sfss);

			logUsedStrategy(confidence, threshold, this);
		}
	};

	abstract <T extends SurfaceForm, U extends Sense> void reviseScores(T surfaceForm,
			List<SurfaceFormSenseScore<T, U>> sfss, double threshold);

	private static final Logger logger = LoggerFactory.getLogger(PriorStrategy.class);

	private static final String CONFIG_PRIOR_STRATEGY = "de.unima.dws.dbpediagraph.disambiguate.priorstrategy";
	private static final String CONFIG_PRIOR_STRATEGY_THRESHOLD = "de.unima.dws.dbpediagraph.disambiguate.priorstrategy.threshold";

	static PriorStrategy fromConfig(Configuration config) {
		return EnumUtils.fromConfig(PriorStrategy.class, config, CONFIG_PRIOR_STRATEGY, true);
	}

	static double getThresholdFromConfig(Configuration config) {
		return config.getDouble(CONFIG_PRIOR_STRATEGY_THRESHOLD);
	}

	/**
	 * Set priors as scores if available for all surface form sense scores
	 */
	private static void assignPriors(SurfaceForm surfaceForm,
			List<? extends SurfaceFormSenseScore<? extends SurfaceForm, ? extends Sense>> sfss) {
		for (SurfaceFormSenseScore<?, ?> surfaceFormSenseScore : sfss) {
			Double prior = surfaceFormSenseScore.getSense().prior();
			if (prior != null)
				surfaceFormSenseScore.setScore(prior);
			else
				logger.warn("{} has no prior.", surfaceFormSenseScore.getSense().fullUri());
		}
	}

	/**
	 * Log the strategy that has been used in prior strategy implementations. Can be used with e.g. grep -c on logfile
	 * to gather usage statistics.
	 */
	private static void logUsedStrategy(double confidence, double threshold, PriorStrategy strategy) {
		if (confidence <= threshold)
			logger.debug("Confidence {} smaller than threshold {}. Use prior strategy with class {}", confidence,
					threshold, strategy.getClass().getSimpleName());
		else
			logger.debug("Confidence {} equal or larger than threshold {}. No prior strategy with class {}",
					confidence, threshold, strategy.getClass().getSimpleName());

	}
}
