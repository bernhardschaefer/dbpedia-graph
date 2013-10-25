package de.unima.dws.dbpediagraph.graphdb.model;


public interface SurfaceFormSenseScore<T extends SurfaceForm, U extends Sense> extends
		Comparable<SurfaceFormSenseScore<T, U>> {

	double getScore();

	U sense();

	void setScore(double score);

	T surfaceForm();

}
