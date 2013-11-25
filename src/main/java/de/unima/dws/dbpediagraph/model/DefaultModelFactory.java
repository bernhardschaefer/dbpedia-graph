package de.unima.dws.dbpediagraph.model;

import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graph.Graphs;

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
	public DefaultSense newSense(String fullUri) {
		return new DefaultSense(fullUri);
	}

	@Override
	public DefaultSense newSense(Vertex v) {
		return newSense(Graphs.uriOfVertex(v));
	}

	@Override
	public DefaultSurfaceForm newSurfaceForm(String name) {
		return new DefaultSurfaceForm(name);
	}

}
