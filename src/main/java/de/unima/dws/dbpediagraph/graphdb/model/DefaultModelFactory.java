package de.unima.dws.dbpediagraph.graphdb.model;

import java.util.Collection;

import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.Graphs;

/**
 * Concrete factory as singleton for creating default sense and surface form
 * instances.
 * 
 * @author Bernhard Schäfer
 * 
 */
public enum DefaultModelFactory implements ModelFactory<DefaultSurfaceForm, DefaultSense> {
	INSTANCE;

	@Override
	public SurfaceFormSenseScore<DefaultSurfaceForm, DefaultSense> newSurfaceFormSenseScore(
			DefaultSurfaceForm surfaceForm, DefaultSense sense, double score) {
		return new DefaultSurfaceFormSenseScore(surfaceForm, sense, score);
	}

	@Override
	public DefaultSense newSense(String fullUri) {
		return new DefaultSense(fullUri);
	}

	@Override
	public DefaultSense newSense(Vertex v) {
		return newSense(Graphs.uriOfVertex(v));
	}

	@Override
	public SurfaceFormSenses<DefaultSurfaceForm, DefaultSense> newSurfaceFormSenses(Collection<DefaultSense> senses,
			String name) {
		return new DefaultSurfaceFormSenses(new DefaultSurfaceForm(name), senses);
	}

}
