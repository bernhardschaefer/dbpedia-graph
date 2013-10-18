package de.unima.dws.dbpediagraph.graphdb.wrapper;

import java.util.Collection;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.oupls.jung.GraphJung;

import de.unima.dws.dbpediagraph.graphdb.Graphs;

public class GraphJungUndirected extends GraphJung<Graph> {

	public GraphJungUndirected(Graph graph) {
		super(graph);
	}

	@Override
	public Collection<Edge> getInEdges(final Vertex vertex) {
		return Graphs.getEdgesOfVertex(vertex, Direction.BOTH);
	}

	@Override
	public Collection<Edge> getOutEdges(final Vertex vertex) {
		return Graphs.getEdgesOfVertex(vertex, Direction.BOTH);
	}

	@Override
	public Collection<Vertex> getSuccessors(final Vertex vertex) {
		return Graphs.getConnectedVerticesBothDirections(vertex);
	}
}
