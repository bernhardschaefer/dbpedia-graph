package de.unima.dws.dbpediagraph.graphdb.util;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.java.GremlinPipeline;

import de.unima.dws.dbpediagraph.graphdb.GraphConfig;

/**
 * Graph Logging and toString helper methods
 * 
 * @author Bernhard Schäfer
 * 
 */
// TODO clean it up
public class GraphPrinter {
	private static final Logger logger = LoggerFactory.getLogger(GraphPrinter.class);

	public static void printEdge(Edge e) {
		logger.info("eid: {} uri: {}", e.getId(), e.getProperty(GraphConfig.URI_PROPERTY));
	}

	public static void printEdges(List<Edge> edgeList) {
		for (Edge e : edgeList) {
			printEdge(e);
		}
	}

	public static void printEdgesOfVertices(Iterator<Vertex> vertices) {
		while (vertices.hasNext()) {
			Vertex v = vertices.next();
			for (Edge e : v.getEdges(Direction.OUT)) {
				logger.info(v.getProperty(GraphConfig.URI_PROPERTY).toString());
				logger.info(e.getProperty(GraphConfig.URI_PROPERTY).toString());
				logger.info(e.getVertex(Direction.IN).getProperty(GraphConfig.URI_PROPERTY).toString());
			}
		}
	}

	public static void printGraphStatistics(Graph graph) {
		Timer t = new Timer();

		long verticesCount = new GremlinPipeline<Object, Object>(graph.getVertices()).count();
		logger.info(String.format("Vertices: %,d", verticesCount));
		t.tick(" count vertices ");

		long edgesCount = new GremlinPipeline<Object, Object>(graph.getEdges()).count();
		logger.info(String.format("Edges: %,d", edgesCount));
		t.tick(" count edges ");

		t.getTime(" total stats ");
	}

	public static void printVertex(Vertex v) {
		String uri = v.getProperty(GraphConfig.URI_PROPERTY);
		logger.info("vid: {} uri: {}", v.getId().toString(), uri);
	}

	/**
	 * Log vertices (at most 100).
	 */
	public static void printVertices(Iterator<Vertex> vertices) {
		printVertices(vertices, 100);
	}

	public static void printVertices(Iterator<Vertex> vertices, int maxCount) {
		while (maxCount-- > 0 && vertices.hasNext()) {
			Vertex v = vertices.next();
			printVertex(v);
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
