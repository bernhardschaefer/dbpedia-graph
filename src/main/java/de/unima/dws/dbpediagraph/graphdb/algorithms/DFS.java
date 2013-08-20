package de.unima.dws.dbpediagraph.graphdb.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.furnace.algorithms.graphcentric.searching.DepthFirstAlgorithm;

import de.unima.dws.dbpediagraph.graphdb.GraphConfig;

public class DFS {
	private static final Logger logger = LoggerFactory.getLogger(DFS.class);

	private static final int MAX_DEPTH = 3;

	public static void main(String[] args) {
		DFS dfs = new DFS();

		int iterations = 100;
		while (iterations-- > 0) {
			List<Edge> edges = dfs.findPathBetweenRandomVertices();
			int pathSize = -1;
			if (edges != null) {
				pathSize = edges.size();
			}
			System.out.println("Path size " + pathSize);

		}

		dfs.graph.shutdown();
	}

	private final TransactionalGraph graph;

	public DFS() {
		graph = GraphConfig.getInstance().getGraph();
	}

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

		// TODO implement EdgeFilter so that only edges with specific uri
		// properties (e.g. rdf:type)
		// for each adjacent
		for (Edge e : start.getEdges(Direction.OUT)) {
			Vertex target = e.getVertex(Direction.OUT);
			if (targets.contains(target)) {
				// TODO: we have a path: throw vertices and edges in subgrapgh
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
		Graph subgraph = null;

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

	public List<Edge> findPath(Vertex start, Vertex target) {
		DepthFirstAlgorithm dfs = new DepthFirstAlgorithm(graph);
		List<Edge> path = dfs.findPathToTarget(start, target);
		return path;
	}

	public List<Edge> findPathBetweenRandomVertices() {
		long startTime = System.currentTimeMillis();
		Random r = new Random();
		int startId = r.nextInt(1_000_000);
		int targetId = r.nextInt(1_000_000);

		Vertex start = graph.getVertex(startId);
		System.out.println(start.getProperty(GraphConfig.URI_PROPERTY));
		Vertex target = graph.getVertex(targetId);
		System.out.println(target.getProperty(GraphConfig.URI_PROPERTY));

		List<Edge> path = findPath(start, target);

		logger.info(String.format("%,.2f secs", (System.currentTimeMillis() - startTime) / 1000.0));
		return path;
	}
}
