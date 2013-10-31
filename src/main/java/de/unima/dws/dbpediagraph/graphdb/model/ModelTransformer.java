package de.unima.dws.dbpediagraph.graphdb.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.Graphs;

/**
 * Noninstantiable Model transformer class that helps to transform between graph-based representations using
 * {@link Vertex} and model-based representations using {@link Sense} and {@link SurfaceForm}.
 * 
 * @author Bernhard Schäfer
 * 
 */
public final class ModelTransformer {
	private static final Logger logger = LoggerFactory.getLogger(ModelTransformer.class);

	public static <U extends Sense> List<U> sensesFromVertices(Collection<Vertex> vertices, ModelFactory<?, U> factory) {
		List<U> senses = new ArrayList<>();
		for (Vertex v : vertices) {
			senses.add(factory.newSense(v));
		}
		return senses;
	}

	public static <T extends SurfaceForm, U extends Sense> Map<T, List<U>> surfaceFormSensesFromVertices(
			Collection<Collection<Vertex>> allWordsSenses, ModelFactory<T, U> factory) {
		Map<T, List<U>> surfaceFormSenses = new HashMap<>();
		for (Collection<Vertex> wordSenses : allWordsSenses)
			surfaceFormSenses.put(factory.newSurfaceForm("test"), sensesFromVertices(wordSenses, factory));
		return surfaceFormSenses;
	}

	public static Collection<Vertex> verticesFromSenses(Graph graph, Collection<? extends Sense> senses) {
		Collection<Vertex> vertices = new ArrayList<>(senses.size());
		for (Sense sense : senses) {
			Vertex v = Graphs.vertexByUri(graph, sense.fullUri());
			if (v != null)
				vertices.add(v);
			else
				logger.warn("No vertex found for uri {}", sense.fullUri());
		}
		return vertices;
	}

	public static Collection<Collection<Vertex>> wordsVerticesFromSenses(Graph graph,
			Map<? extends SurfaceForm, ? extends List<? extends Sense>> sFSenses) {
		Collection<Collection<Vertex>> wordVertices = new ArrayList<>();
		for (List<? extends Sense> senses : sFSenses.values()) {
			Collection<Vertex> vertices = verticesFromSenses(graph, senses);
			if (!vertices.isEmpty())
				wordVertices.add(vertices);
		}
		return wordVertices;
	}

	// suppress default constructor for noninstantiability
	private ModelTransformer() {
		throw new AssertionError();
	}
}
