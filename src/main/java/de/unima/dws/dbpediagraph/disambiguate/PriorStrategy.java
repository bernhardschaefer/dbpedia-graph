package de.unima.dws.dbpediagraph.disambiguate;

import java.util.List;

import de.unima.dws.dbpediagraph.model.*;

/**
 * Prior strategy interface to revise graph-based entity candidate scores based on their scores and prior probability
 * information.
 * 
 * @author Bernhard Schäfer
 * 
 */
interface PriorStrategy {
	<T extends SurfaceForm, U extends Sense> void reviseScores(T surfaceForm, List<SurfaceFormSenseScore<T, U>> sfss);
}
