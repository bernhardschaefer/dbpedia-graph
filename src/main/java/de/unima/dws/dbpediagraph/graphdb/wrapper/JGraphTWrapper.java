package de.unima.dws.dbpediagraph.graphdb.wrapper;

import java.util.HashSet;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.AbstractGraph;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

public class JGraphTWrapper extends AbstractGraph<Vertex, Edge> implements DirectedGraph<Vertex, Edge> {

	private final Graph graph;

	public JGraphTWrapper(Graph graph) {
		this.graph = graph;
	}

	public Graph getRawGraph() {
		return graph;
	}

	@Override
	public Set<Edge> getAllEdges(Vertex sourceVertex, Vertex targetVertex) {
		Set<Edge> sourceOut = outgoingEdgesOf(sourceVertex);

		Set<Edge> edges = new HashSet<Edge>();
		for (Edge e : sourceOut) {
			if (e.getVertex(Direction.IN).equals(targetVertex)) {
				edges.add(e);
			}
		}

		return edges;
	}

	@Override
	public Edge getEdge(Vertex sourceVertex, Vertex targetVertex) {
		Set<Edge> allEdges = getAllEdges(sourceVertex, targetVertex);
		return allEdges.iterator().hasNext() ? allEdges.iterator().next() : null;
	}

	@Override
	public EdgeFactory<Vertex, Edge> getEdgeFactory() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Edge addEdge(Vertex sourceVertex, Vertex targetVertex) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addEdge(Vertex sourceVertex, Vertex targetVertex, Edge e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addVertex(Vertex v) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsEdge(Edge e) {
		return graph.getEdge(e.getId()) != null;
	}

	@Override
	public boolean containsVertex(Vertex v) {
		return graph.getVertex(v.getId()) != null;
	}

	@Override
	public Set<Edge> edgeSet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<Edge> edgesOf(Vertex vertex) {
		return getEdgesOfVertex(vertex, Direction.BOTH);
	}

	@Override
	public Edge removeEdge(Vertex sourceVertex, Vertex targetVertex) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeEdge(Edge e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeVertex(Vertex v) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<Vertex> vertexSet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Vertex getEdgeSource(Edge e) {
		return e.getVertex(Direction.IN);
	}

	@Override
	public Vertex getEdgeTarget(Edge e) {
		return e.getVertex(Direction.OUT);
	}

	@Override
	public double getEdgeWeight(Edge e) {
		return 1;
	}

	@Override
	public int inDegreeOf(Vertex vertex) {
		return incomingEdgesOf(vertex).size();
	}

	@Override
	public Set<Edge> incomingEdgesOf(Vertex vertex) {
		return getEdgesOfVertex(vertex, Direction.IN);
	}

	private Set<Edge> getEdgesOfVertex(Vertex vertex, Direction d) {
		Set<Edge> edges = new HashSet<>();
		Iterable<Edge> edgeIter = vertex.getEdges(d);
		for (Edge e : edgeIter) {
			edges.add(e);
		}
		return edges;
	}

	@Override
	public int outDegreeOf(Vertex vertex) {
		return outgoingEdgesOf(vertex).size();
	}

	@Override
	public Set<Edge> outgoingEdgesOf(Vertex vertex) {
		return getEdgesOfVertex(vertex, Direction.OUT);
	}

}
