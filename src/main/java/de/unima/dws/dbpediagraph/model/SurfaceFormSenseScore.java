package de.unima.dws.dbpediagraph.model;

/**
 * Holder of a score for a {@link Sense} that corresponds to a {@link SurfaceForm}.
 * 
 * @author Bernhard Schäfer
 * 
 */
public interface SurfaceFormSenseScore<T extends SurfaceForm, U extends Sense> extends
		Comparable<SurfaceFormSenseScore<T, U>> {
	double score();

	U sense();

	T surfaceForm();
}
