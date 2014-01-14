package de.unima.dws.dbpediagraph.disambiguate;

import java.util.Collections;
import java.util.List;

import de.unima.dws.dbpediagraph.model.*;

/**
 * Deletes all entity candidates of a surface form if they are all singletons, meaning they do not have any connections
 * in the subgraph.
 * 
 * @author Bernhard Schäfer
 * 
 */
class NoAnnotationPriorStrategy implements PriorStrategy {

	@Override
	public <T extends SurfaceForm, U extends Sense> void reviseScores(T surfaceForm,
			List<SurfaceFormSenseScore<T, U>> sfss) {
		// check if there are only singletons
		// TODO compare max with threshold
		if (!sfss.isEmpty() // Prevent NoSuchElementException from Collections.max() if there are no candidates
				&& Collections.max(sfss, SurfaceFormSenseScore.ASCENDING_SCORE_COMPARATOR).getScore() <= 0.0) {
			sfss.clear(); // delete singleton candidates to prevent random selection of a singleton
		}

	}
}
