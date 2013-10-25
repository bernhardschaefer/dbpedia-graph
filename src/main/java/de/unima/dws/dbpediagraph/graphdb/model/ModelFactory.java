package de.unima.dws.dbpediagraph.graphdb.model;

import java.util.Collection;

import com.tinkerpop.blueprints.Vertex;

/**
 * Abstract factory pattern.
 * 
 * @author Bernhard Schäfer
 */
public interface ModelFactory<T extends SurfaceForm, U extends Sense> {
	SurfaceFormSenseScore<T, U> createInitialSurfaceFormSenseScore(T surfaceForm, U sense);

	U newSense(String uri);

	U newSense(Vertex v);

	SurfaceFormSenses<T, U> newSurfaceFormSenses(Collection<U> senses, String name);
}
