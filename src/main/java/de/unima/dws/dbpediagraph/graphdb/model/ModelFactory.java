package de.unima.dws.dbpediagraph.graphdb.model;

import com.tinkerpop.blueprints.Vertex;

/**
 * Abstract factory for creating sense and surface form related instances.
 * 
 * @author Bernhard Schäfer
 */
public interface ModelFactory<T extends SurfaceForm, U extends Sense> {
	U newSense(String uri);

	U newSense(Vertex v);

	T newSurfaceForm(String name);

	SurfaceFormSenseScore<T, U> newSurfaceFormSenseScore(T surfaceForm, U sense, double score);
}
