package de.unima.dws.dbpediagraph.disambiguate;

import java.util.Collections;
import java.util.List;

import de.unima.dws.dbpediagraph.model.*;

/**
 * Confidence-based prior strategy that assigns prior probabilities to each score of all candidate entities of a surface
 * form if the confidence for the first candidate falls below a specified threshold. In this implementation the
 * confidence is estimated as e^(score-2nd-candidate - score-1st-candidate).
 * 
 * @author Bernhard Schäfer
 * 
 */
class ConfidenceFallbackPriorStrategy implements PriorStrategy {
	private final double threshold;

	public ConfidenceFallbackPriorStrategy(double threshold) {
		this.threshold = threshold;
	}

	@Override
	public <T extends SurfaceForm, U extends Sense> void reviseScores(T surfaceForm,
			List<SurfaceFormSenseScore<T, U>> sfss) {
		// only check confidence if we more than one 1 candidate
		// if there is only one candidate we will assume that this candidate is likely correct
		if (sfss.size() > 1) {
			Collections.sort(sfss, SurfaceFormSenseScore.DESCENDING_SCORE_COMPARATOR);
			SurfaceFormSenseScore<T, U> first = sfss.get(0);
			SurfaceFormSenseScore<T, U> second = sfss.get(1);

			// confidence calculation from DBTwoStepDisambiguator.bestK_()
			double confidence = Math.exp(second.getScore() - first.getScore());
			if (confidence < threshold) {
				PriorStrategyFactory.assignPriors(surfaceForm, sfss);
			}
		}
	}
}
