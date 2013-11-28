package de.unima.dws.dbpediagraph.subgraph;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.*;

import de.unima.dws.dbpediagraph.graph.*;
import de.unima.dws.dbpediagraph.util.Counter;

/**
 * Noninstantiable utility class for static methods helping in {@link SubgraphConstruction}.
 * 
 * @author Bernhard Schäfer
 * 
 */
final class SubgraphConstructions {
	private static final Logger logger = LoggerFactory.getLogger(SubgraphConstructions.class);

	public static void addIntermediateNodes(List<Edge> path, Set<Vertex> vertices) {
		if (path.size() > 1) {
			List<Edge> intermedPath = path.subList(1, path.size());
			for (Edge e : intermedPath) {
				Vertex v = e.getVertex(Direction.OUT);
				vertices.add(v);
			}
		}
	}

	public static void addPathToSubGraph(Vertex target, Path path, Graph subGraph, GraphType graphType) {
		addPathToSubGraph(path.getStart(), target, path.getEdges(), subGraph, graphType);
	}

	public static void addPathToSubGraph(Vertex start, Vertex target, List<Edge> path, Graph subGraph,
			GraphType graphType) {
		logger.debug("Found sense vid: {} uri: {}", target.getId(), target.getProperty(GraphConfig.URI_PROPERTY));
		logger.debug(toStringPath(path, start, target, graphType));
		Graphs.addNodeAndEdgesByIdIfNonExistent(subGraph, path);
	}

	public static void checkValidSenses(Graph graph, Collection<Vertex> senses) {
		if (senses == null)
			throw new NullPointerException("The senses collection cannot be null.");
		for (Vertex v : senses) {
			if (v == null) {
				throw new IllegalArgumentException("Vertex cannot be null.");
			}
			if (graph.getVertex(v.getId()) == null)
				throw new IllegalArgumentException(String.format(
						"The vertex vid:%s uri:%s does not belong to this graph.", v.getId(),
						v.getProperty(GraphConfig.URI_PROPERTY)));
		}
	}

	public static void checkValidWordsSenses(Graph graph, Collection<? extends Collection<Vertex>> wordsSenses) {
		if (wordsSenses == null)
			throw new NullPointerException("The senses collection cannot be null.");
		for (Collection<Vertex> senses : wordsSenses)
			checkValidSenses(graph, senses);

	}

	public static void logSubgraphConstructionStats(Logger logger, Class<?> clazz, Graph subgraph, long startTimeNano,
			long traversedNodes, int maxDistance) {
		// tradeoff between better performance of curly brackets notation
		// vs. more convenient comma-separated traversed nodes
		logger.info(String
				.format("%s Stats: time [sec]: %.2f, subgraph vertices: %d, subgraph edges: %d, traversed nodes: %,d, max distance: %d",
						clazz.getSimpleName(), Counter.elapsedSecs(startTimeNano), Graphs.verticesCount(subgraph),
						Graphs.edgesCount(subgraph), traversedNodes, maxDistance));
	}

	private static String toStringPath(List<Edge> path, Vertex start, Vertex end, GraphType graphType) {
		if (graphType == null)
			throw new NullPointerException("Graph type cannot be null");
		if (path.size() == 0)
			return null;

		StringBuilder builder = new StringBuilder();
		switch (graphType) {
		case DIRECTED_GRAPH:
			for (Edge e : path)
				builder.append(e.getVertex(Direction.OUT).getProperty(GraphConfig.URI_PROPERTY)).append("--")
						.append(e.getProperty(GraphConfig.URI_PROPERTY)).append("-->");
			break;
		case UNDIRECTED_GRAPH:
			Vertex from = start;
			for (Edge e : path) {
				builder.append(from.getProperty(GraphConfig.URI_PROPERTY)).append("--")
						.append(e.getProperty(GraphConfig.URI_PROPERTY)).append("--");
				from = Graphs.oppositeVertexUnsafe(e, from);
			}
			break;
		}

		builder.append(end.getProperty(GraphConfig.URI_PROPERTY));

		return builder.toString();
	}

	// Suppress default constructor for noninstantiability
	private SubgraphConstructions() {
		throw new AssertionError();
	}
}
