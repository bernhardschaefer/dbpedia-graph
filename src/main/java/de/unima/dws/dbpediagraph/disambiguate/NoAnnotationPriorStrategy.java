package de.unima.dws.dbpediagraph.disambiguate;

import java.util.Collections;
import java.util.List;

import de.unima.dws.dbpediagraph.model.*;

/**
 * Deletes all entity candidates of a surface form if their highest score falls below a certain threshold. If the
 * threshold is defined as zero, this strategy prevents the random selection of a candidate if all candidates are
 * singletons, meaning they do not have any connections in the subgraph.
 * 
 * @author Bernhard Schäfer
 * 
 */
class NoAnnotationPriorStrategy implements PriorStrategy {
	private final double threshold;

	public NoAnnotationPriorStrategy(double threshold) {
		this.threshold = threshold;
	}

	@Override
	public <T extends SurfaceForm, U extends Sense> void reviseScores(T surfaceForm,
			List<SurfaceFormSenseScore<T, U>> sfss) {
		double confidence = sfss.isEmpty() ? 1.0 : Collections.max(sfss,
				SurfaceFormSenseScore.ASCENDING_SCORE_COMPARATOR).getScore();
		if (confidence <= threshold)
			sfss.clear(); // delete all candidates so annotation is done

		PriorStrategyFactory.logUsedStrategy(confidence, threshold, this);
	}
}
