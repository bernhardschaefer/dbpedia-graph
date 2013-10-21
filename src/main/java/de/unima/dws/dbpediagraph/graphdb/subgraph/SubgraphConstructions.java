package de.unima.dws.dbpediagraph.graphdb.subgraph;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.GraphConfig;
import de.unima.dws.dbpediagraph.graphdb.GraphType;
import de.unima.dws.dbpediagraph.graphdb.Graphs;

/**
 * Noninstantiable utility class for static methods helping in {@link SubgraphConstruction}.
 * 
 * @author Bernhard Schäfer
 * 
 */
class SubgraphConstructions {
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
		Graphs.addNodeAndEdgesIfNonExistent(subGraph, path);
	}

	public static void checkValidSenses(Graph graph, Collection<Vertex> senses) {
		if (senses == null)
			throw new NullPointerException("The senses collection cannot be null.");
		if (senses.size() == 0)
			throw new IllegalArgumentException("The senses collection cannot be empty.");
		for (Vertex v : senses)
			if (graph.getVertex(v.getId()) == null)
				throw new IllegalArgumentException(String.format(
						"The vertex vid:%s uri:%s does not belong to this graph.", v.getId(),
						v.getProperty(GraphConfig.URI_PROPERTY)));
	}

	public static void checkValidWordsSenses(Graph graph, Collection<Collection<Vertex>> wordsSenses) {
		if (wordsSenses == null)
			throw new NullPointerException("The senses collection cannot be null.");
		if (wordsSenses.size() == 0)
			throw new IllegalArgumentException("The senses collection cannot be empty.");
		for (Collection<Vertex> senses : wordsSenses)
			checkValidSenses(graph, senses);

	}

	public static void logSubgraphConstructionStats(Logger logger, Class<?> clazz, Graph subgraph, long startTime,
			long traversedNodes, int maxDistance) {
		// logger.info("{}: time {} sec., traversed nodes: {}, maxDepth: {}", clazz.getSimpleName(),
		// (System.currentTimeMillis() - startTime) / 1000.0, traversedNodes, maxDistance);
		logger.info(String.format("%s: time %.2f sec., traversed nodes: %,d, maxDepth: %d", clazz.getSimpleName(),
				(System.currentTimeMillis() - startTime) / 1000.0, traversedNodes, maxDistance));
		if (logger.isDebugEnabled())
			logger.debug("Subgraph vertices:{}, edges:{}", Graphs.numberOfVertices(subgraph),
					Graphs.numberOfEdges(subgraph));
	}

	public static String toStringPath(List<Edge> path, Vertex start, Vertex end, GraphType graphType) {
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
