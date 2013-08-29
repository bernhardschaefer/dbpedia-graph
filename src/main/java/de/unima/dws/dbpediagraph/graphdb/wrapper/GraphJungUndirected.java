package de.unima.dws.dbpediagraph.graphdb.wrapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.oupls.jung.GraphJung;

import de.unima.dws.dbpediagraph.graphdb.GraphUtil;

public class GraphJungUndirected extends GraphJung<Graph> {

	public GraphJungUndirected(Graph graph) {
		super(graph);
	}

	@Override
	public Collection<Edge> getInEdges(final Vertex vertex) {
		final Iterable<Edge> itty = vertex.getEdges(Direction.BOTH);
		if (itty instanceof Collection) {
			return (Collection<Edge>) itty;
		} else {
			final List<Edge> edges = new ArrayList<Edge>();
			for (final Edge edge : itty) {
				edges.add(edge);
			}
			return edges;
		}
	}

	@Override
	public Collection<Vertex> getSuccessors(final Vertex vertex) {
		return GraphUtil.getConnectedVerticesBothDirections(vertex);
	}
}
