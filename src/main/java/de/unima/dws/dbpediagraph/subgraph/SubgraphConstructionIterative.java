package de.unima.dws.dbpediagraph.subgraph;

import java.util.*;

import com.google.common.collect.Iterables;
import com.tinkerpop.blueprints.*;

import de.unima.dws.dbpediagraph.graph.GraphType;
import de.unima.dws.dbpediagraph.graph.Graphs;

/**
 * Iterative implementation for constructing a subgraph
 * 
 * @author Bernhard Schäfer
 * 
 */
class SubgraphConstructionIterative extends AbstractSubgraphConstruction implements SubgraphConstruction {
	private static final long TRAVERSAL_TICK_RATE = 1_000_000;

	public SubgraphConstructionIterative(Graph graph, SubgraphConstructionSettings settings) {
		super(graph, settings);
	}

	@Override
	protected void dfs(Path path, Set<Vertex> targets, Graph subgraph, Set<Vertex> stopVertices) {
		if (targets.isEmpty()) // do nothing if there are no targets
			return;

		Deque<Path> stack = new ArrayDeque<>();
		stack.push(path);
		while (!stack.isEmpty()) {
			traversedNodes++;
			if (traversedNodes % TRAVERSAL_TICK_RATE == 0)
				logger.info("{} traversed nodes", traversedNodes);

			path = stack.pop();
			Vertex current = path.getLast();

			// check if target node
			if (targets.contains(current))
				SubgraphConstructions.addPathToSubGraph(current, path, subgraph, settings.graphType);

			// do not explore further if we are at max distance already
			if (path.getEdges().size() >= settings.maxDistance)
				continue;

			// explore further
			for (Edge edge : Iterables.filter(current.getEdges(settings.graphType.getTraversalDirection()),
					settings.edgeFilter)) { // get all edges in traversal direction that are not being filtered
				Vertex child = Graphs.oppositeVertexUnsafe(edge, current);

				// According to Navigli&Lapata algorithm, do not accept paths crossing vertices of the source
				// surface form.
				if (!path.getVertices().contains(child) && !stopVertices.contains(child)) {
					// for undirected graph check if vertex/edge combination is worth exploring
					if (settings.graphType.equals(GraphType.UNDIRECTED_GRAPH)
							&& !settings.explorationThreshold.isBelowThreshold(child, edge))
						continue;

					Path newPath = Path.newHop(path, edge, child);
					stack.push(newPath);
					// recursive: dfs(newPath, targets, subGraph, stopSenses);
				}
			}
		}
	}
}
