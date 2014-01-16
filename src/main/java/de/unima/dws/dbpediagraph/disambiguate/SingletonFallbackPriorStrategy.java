package de.unima.dws.dbpediagraph.disambiguate;

import java.util.Collections;
import java.util.List;

import de.unima.dws.dbpediagraph.model.*;

/**
 * Assign the prior probability as the score of all entity candidates of a surface form if all entity candidates are
 * unconnected, which means they have a score of zero.
 * 
 * @author Bernhard Schäfer
 * 
 */
// TODO change to MinScoreFallbackPriorStrategy; change enum; add explanation that threshold == 0 means
// SingletonFallback
class SingletonFallbackPriorStrategy implements PriorStrategy {
	private final double threshold;

	public SingletonFallbackPriorStrategy(double threshold) {
		this.threshold = threshold;
	}

	@Override
	public <T extends SurfaceForm, U extends Sense> void reviseScores(T surfaceForm,
			List<SurfaceFormSenseScore<T, U>> sfss) {
		// check if there are only singletons
		double confidence = sfss.isEmpty() ? 1.0 : Collections.max(sfss,
				SurfaceFormSenseScore.ASCENDING_SCORE_COMPARATOR).getScore();
		if (confidence <= threshold)
			PriorStrategyFactory.assignPriors(surfaceForm, sfss);

		PriorStrategyFactory.logUsedStrategy(confidence, threshold, this);
	}
}
