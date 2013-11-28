package de.unima.dws.dbpediagraph.model;

import java.util.*;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graph.Graphs;

/**
 * Noninstantiable Model transformer class that helps to transform between model-based representations using
 * {@link Sense} and {@link SurfaceForm} and graph-based representations using {@link Vertex}.
 * 
 * @author Bernhard Schäfer
 * 
 */
public final class ModelToVertex {

	public static Set<Vertex> verticesFromSenses(Graph graph, Collection<? extends Sense> senses) {
		Set<Vertex> vertices = new HashSet<>(senses.size());
		for (Sense sense : senses) {
			Vertex v = Graphs.vertexByUri(graph, sense.fullUri());
			if (v != null)
				vertices.add(v);
		}
		return vertices;
	}

	/**
	 * Transform a model-based representation of surface forms and their sense candidates into a nested collection of
	 * vertices, where each inner collection represents the sense candidates of a surface form. Note that during
	 * transformation information about surface form names is getting lost.
	 * 
	 * @param graph
	 *            the graph used for retrieving the vertices
	 */
	public static Collection<Set<Vertex>> verticesFromSurfaceFormSenses(Graph graph,
			Map<? extends SurfaceForm, ? extends List<? extends Sense>> sFSenses) {
		return verticesFromNestedSenses(graph, sFSenses.values());
	}

	/**
	 * Transform a model-based representation of sense candidates into a nested collection of vertices, where each inner
	 * collection represents the sense candidates of a surface form.
	 * 
	 * @param graph
	 *            the graph used for retrieving the vertices
	 */
	public static Collection<Set<Vertex>> verticesFromNestedSenses(Graph graph,
			Collection<? extends List<? extends Sense>> senses) {
		Collection<Set<Vertex>> senseVertices = new ArrayList<>();
		for (List<? extends Sense> sFSenses : senses) {
			Set<Vertex> vertices = verticesFromSenses(graph, sFSenses);
			if (!vertices.isEmpty())
				senseVertices.add(vertices);
		}
		return senseVertices;
	}

	// suppress default constructor for noninstantiability
	private ModelToVertex() {
		throw new AssertionError();
	}
}
