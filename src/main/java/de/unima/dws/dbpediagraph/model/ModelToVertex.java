package de.unima.dws.dbpediagraph.model;

import java.util.*;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private static final Logger LOGGER = LoggerFactory.getLogger(ModelToVertex.class);

	public static Set<Vertex> verticesFromSenses(Graph graph, Collection<? extends Sense> senses) {
		Set<Vertex> vertices = new HashSet<>(senses.size());
		for (Sense sense : senses) {
			Vertex v = Graphs.vertexByFullUri(graph, sense.fullUri());
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
	 * @param mergeEqualSurfaceForms
	 *            keep only one set of candidates for multiple surface forms with equal names
	 */
	public static Collection<Set<Vertex>> verticesFromSurfaceFormSenses(Graph graph,
			Map<? extends SurfaceForm, ? extends List<? extends Sense>> sfsSenses, boolean mergeEqualSurfaceForms) {
		Collection<Set<Vertex>> senseVertices = new ArrayList<>();
		Set<String> sfUris = new HashSet<>();
		for (Entry<? extends SurfaceForm, ? extends List<? extends Sense>> entry : sfsSenses.entrySet()) {
			if (mergeEqualSurfaceForms && !sfUris.add(entry.getKey().name())) {
				LOGGER.debug("Skipping sf since the name exists more than once: {}", entry.getKey());
				continue;
			}
			Set<Vertex> vertices = verticesFromSenses(graph, entry.getValue());
			if (!vertices.isEmpty())
				senseVertices.add(vertices);
		}
		if (senseVertices.isEmpty())
			LOGGER.warn("No vertices found for {}", sfsSenses);
		return senseVertices;
	}

	// suppress default constructor for noninstantiability
	private ModelToVertex() {
		throw new AssertionError();
	}
}
