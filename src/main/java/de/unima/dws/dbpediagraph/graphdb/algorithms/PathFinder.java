package de.unima.dws.dbpediagraph.graphdb.algorithms;

import org.jgrapht.alg.KShortestPaths;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.GraphProvider;

public class PathFinder {
	private final com.tinkerpop.blueprints.Graph graph = GraphProvider.getInstance().getGraph();
	private org.jgrapht.DirectedGraph<Vertex, Edge> jgraph;

	public static void main(String[] args) {
		PathFinder pf = new PathFinder();
	}

	public void findPath(Vertex startVertex, Vertex endVertex, int nMaxHops, int nPaths) {
		KShortestPaths<Vertex, Edge> paths = new KShortestPaths<Vertex, Edge>(jgraph, startVertex, nPaths, nMaxHops);
		paths.getPaths(endVertex);
	}

}
