package de.unima.dws.dbpediagraph.graphdb.model;

import java.util.Collection;

import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.Graphs;

public enum DefaultModelFactory implements ModelFactory<DefaultSurfaceForm, DefaultSense> {
	INSTANCE;

	private static final double DEFAULT_SCORE = 0;

	@Override
	public SurfaceFormSenseScore<DefaultSurfaceForm, DefaultSense> createInitialSurfaceFormSenseScore(
			DefaultSurfaceForm surfaceForm, DefaultSense sense) {
		return new DefaultSurfaceFormSenseScore(surfaceForm, sense, DEFAULT_SCORE);
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
