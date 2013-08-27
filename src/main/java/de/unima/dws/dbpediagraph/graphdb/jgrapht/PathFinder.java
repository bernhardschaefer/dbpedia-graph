package de.unima.dws.dbpediagraph.graphdb.jgrapht;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.DirectedGraph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.KShortestPaths;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.GraphConfig;
import de.unima.dws.dbpediagraph.graphdb.GraphUtil;
import de.unima.dws.dbpediagraph.graphdb.GraphProvider;
import de.unima.dws.dbpediagraph.graphdb.util.GraphPrinter;

public class PathFinder {
	public static void main(String[] args) {
		PathFinder pf = new PathFinder();
		pf.demo();
	}

	private final DirectedGraph<Vertex, Edge> jgraph;
	private final Graph rawGraph;

	public PathFinder() {
		rawGraph = GraphProvider.getInstance().getGraph();
		jgraph = new JGraphTWrapper(rawGraph);
	}

	private void demo() {
		String[] resources = new String[] { "Michael_I._Jordan", "Michael_Jordan", "Machine_learning",
				"Artificial_intelligence" };

		Collection<Vertex> vertices = new LinkedList<Vertex>();
		for (String resource : resources) {
			String uri = GraphConfig.DBPEDIA_RESOURCE_PREFIX + resource;
			vertices.add(GraphUtil.getVertexByUri(rawGraph, uri));
		}

		int nMaxHops = 10;
		int nPaths = 3;

		for (Vertex v1 : vertices) {
			for (Vertex v2 : vertices) {
				if (!v1.equals(v2)) {
					List<GraphPath<Vertex, Edge>> paths = findPath(v1, v2, nMaxHops, nPaths);
					for (GraphPath<Vertex, Edge> path : paths) {
						GraphPrinter.printVertex(path.getStartVertex());
						GraphPrinter.printVertex(path.getEndVertex());
						GraphPrinter.printEdges(path.getEdgeList());

					}
				}
			}

		}

	}

	public List<GraphPath<Vertex, Edge>> findPath(Vertex startVertex, Vertex endVertex, int nMaxHops, int nPaths) {
		KShortestPaths<Vertex, Edge> paths = new KShortestPaths<Vertex, Edge>(jgraph, startVertex, nPaths, nMaxHops);
		return paths.getPaths(endVertex);
	}

}
