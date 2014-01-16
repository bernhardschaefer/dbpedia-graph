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
// TODO rename; this is not prior strategy but more like graphConfidenceThresholdStrategy since e.g. with NoAnnotation
// priors are not used
interface PriorStrategy {
	<T extends SurfaceForm, U extends Sense> void reviseScores(T surfaceForm, List<SurfaceFormSenseScore<T, U>> sfss);
}
