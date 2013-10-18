package de.unima.dws.dbpediagraph.graphdb.util;

import java.util.List;

import org.slf4j.Logger;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.GraphConfig;
import de.unima.dws.dbpediagraph.graphdb.Graphs;

/**
 * Graph Logging and toString helper methods
 * 
 * @author Bernhard Schäfer
 * 
 */
public class GraphPrinter {

	public static void logSubgraphConstructionStats(Logger logger, Class<?> clazz, Graph subgraph, long startTime,
			int traversedNodes, int maxDistance) {
		logger.info("{}: time {} sec., traversed nodes: {}, maxDepth: {}", clazz.getSimpleName(),
				(System.currentTimeMillis() - startTime) / 1000.0, traversedNodes, maxDistance);
		if (logger.isDebugEnabled()) {
			logger.debug("Subgraph vertices:{}, edges:{}", Graphs.getNumberOfVertices(subgraph),
					Graphs.getNumberOfEdges(subgraph));
		}
	}

	public static String toStringPath(List<Edge> path, Vertex start, Vertex end) {
		if (path.size() == 0) {
			return null;
		}

		StringBuilder builder = new StringBuilder();
		for (Edge e : path) {
			builder.append(e.getVertex(Direction.OUT).getProperty(GraphConfig.URI_PROPERTY)).append("--")
					.append(e.getProperty(GraphConfig.URI_PROPERTY)).append("-->");
		}
		builder.append(end.getProperty(GraphConfig.URI_PROPERTY));
		return builder.toString();

	}
}
