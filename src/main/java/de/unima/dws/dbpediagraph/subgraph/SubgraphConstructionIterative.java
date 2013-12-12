package de.unima.dws.dbpediagraph.subgraph;

import java.util.*;

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

	public SubgraphConstructionIterative(Graph graph, SubgraphConstructionSettings settings) {
		super(graph, settings);
	}

	@Override
	protected void dfs(Path path, Set<Vertex> targets, Graph subgraph, Set<Vertex> stopVertices) {
		Deque<Path> stack = new ArrayDeque<>();
		stack.push(path);
		while (!stack.isEmpty()) {
			traversedNodes++;

			path = stack.pop();
			Vertex current = path.getLast();

			// check limit
			if (path.getEdges().size() > settings.maxDistance)
				continue;

			// check if target node
			if (targets.contains(current))
				SubgraphConstructions.addPathToSubGraph(current, path, subgraph, settings.graphType);

			// explore further
			for (Edge edge : current.getEdges(settings.graphType.getTraversalDirection())) {
				Vertex child = Graphs.oppositeVertexUnsafe(edge, current);

				// for undirected graph check if vertex/edge combination is worth exploring
				if (settings.graphType.equals(GraphType.UNDIRECTED_GRAPH)
						&& !settings.explorationThreshold.isBelowThreshold(child, edge))
					continue;

				// According to Navigli&Lapata algorithm, do not accept paths crossing vertices of the source
				// surface form.
				if (!path.getVertices().contains(child) && !stopVertices.contains(child)) {
					Path newPath = Path.newHop(path, edge, child);
					stack.push(newPath);
					// recursive: dfs(newPath, targets, subGraph, stopSenses);
				}
			}
		}
	}
}
