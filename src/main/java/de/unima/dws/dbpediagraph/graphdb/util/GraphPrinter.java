package de.unima.dws.dbpediagraph.graphdb.util;

import java.util.List;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.GraphConfig;

/**
 * Graph Logging and toString helper methods
 * 
 * @author Bernhard Schäfer
 * 
 */
public class GraphPrinter {

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
