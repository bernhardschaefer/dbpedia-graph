package de.unima.dws.dbpediagraph.graphdb.subgraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;

class DFS {
	private static final int MAX_DEPTH = 3;

	public void createSubgraph(Graph subgraph, List<Vertex> vertices, List<Edge> edges, Set<Vertex> done, int depth,
			Collection<Vertex> targets) {
		Vertex start = vertices.get(vertices.size() - 1);
		if (done.contains(start))
			return;
		if (depth > MAX_DEPTH)
			return;

		// CHECK: need new?
		final Set<Vertex> newDone = new HashSet<>(done);
		newDone.add(start);

		// for each adjacent
		for (Edge e : start.getEdges(Direction.OUT)) {
			Vertex target = e.getVertex(Direction.OUT);
			if (targets.contains(target)) {
				// we have a path: throw vertices and edges in subgrapgh
			}
			List<Vertex> newVertices = new ArrayList<>(vertices);
			newVertices.add(target);
			List<Edge> newEdges = new ArrayList<>(edges);
			newEdges.add(e);

			createSubgraph(subgraph, newVertices, newEdges, newDone, depth + 1, targets);
		}
	}

	public Graph createSubgraph(List<Vertex> context) {

		// get it from somewhere...
		Graph subgraph = new TinkerGraph();

		for (Vertex start : context) {

			List<Vertex> targets = new ArrayList<>(context);
			context.remove(start);

			List<Vertex> vertices = new ArrayList<>();
			vertices.add(start);
			List<Edge> edges = new ArrayList<>();

			Set<Vertex> done = new HashSet<>();

			createSubgraph(subgraph, vertices, edges, done, 0, targets);
		}

		return subgraph;
	}

}
