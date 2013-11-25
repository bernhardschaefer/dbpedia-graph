package de.unima.dws.dbpediagraph.util;

import java.util.Collection;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.oupls.jung.GraphJung;

import de.unima.dws.dbpediagraph.graph.Graphs;

public class GraphJungUndirectedWrapper extends GraphJung<Graph> {

	public GraphJungUndirectedWrapper(Graph graph) {
		super(graph);
	}

	@Override
	public Collection<Edge> getInEdges(final Vertex vertex) {
		return Graphs.connectedEdges(vertex, Direction.BOTH);
	}

	@Override
	public Collection<Edge> getOutEdges(final Vertex vertex) {
		return Graphs.connectedEdges(vertex, Direction.BOTH);
	}

	@Override
	public Collection<Vertex> getSuccessors(final Vertex vertex) {
		return Graphs.connectedVerticesBothDirections(vertex);
	}
}
