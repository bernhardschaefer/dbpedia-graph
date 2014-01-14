package de.unima.dws.dbpediagraph.disambiguate;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unima.dws.dbpediagraph.model.*;

/**
 * Assign the prior probability as the score of all entity candidates of a surface form if all entity candidates are
 * unconnected, which means they have a score of zero.
 * 
 * @author Bernhard Schäfer
 * 
 */
class SingletonFallbackPriorStrategy implements PriorStrategy {
	private static final Logger logger = LoggerFactory.getLogger(NoAnnotationPriorStrategy.class);

	@Override
	public <T extends SurfaceForm, U extends Sense> void reviseScores(T surfaceForm,
			List<SurfaceFormSenseScore<T, U>> sfss) {
		// check if there are only singletons
		// TODO compare max with threshold
		if (!sfss.isEmpty() // Prevent NoSuchElementException from Collections.max() if there are no candidates
				&& Collections.max(sfss, SurfaceFormSenseScore.ASCENDING_SCORE_COMPARATOR).getScore() <= 0.0) {
			logger.debug("Surface form {} has only candidate singletons {}. Using priors.", surfaceForm, sfss);
			PriorStrategyFactory.assignPriors(surfaceForm, sfss);
		}
	}
}
