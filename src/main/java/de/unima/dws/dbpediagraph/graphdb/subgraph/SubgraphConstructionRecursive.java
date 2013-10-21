package de.unima.dws.dbpediagraph.graphdb.subgraph;

import java.util.Set;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.Graphs;

/**
 * Recursive implementation for constructing a subgraph
 * 
 * @author Bernhard Schäfer
 * 
 */
class SubgraphConstructionRecursive extends AbstractSubgraphConstruction implements SubgraphConstruction {
	public SubgraphConstructionRecursive(Graph graph, SubgraphConstructionSettings settings) {
		super(graph, settings);
	}

	@Override
	protected void dfs(Path path, Set<Vertex> targets, Graph subGraph) {
		traversedNodes++;

		Vertex current = path.getLast();

		// check limit
		if (path.getEdges().size() > settings.maxDistance) {
			return;
		}

		// check if target node
		if (targets.contains(current)) {
			SubgraphConstructions.addPathToSubGraph(current, path, subGraph, settings.graphType);
		}

		// explore further
		for (Edge edge : Graphs.connectedEdges(current, settings.graphType)) {
			Vertex child = Graphs.oppositeVertexUnsafe(edge, current);
			if (!path.getVertices().contains(child)) {
				Path newPath = Path.newHop(path, edge, child);
				dfs(newPath, targets, subGraph);
			}
		}
	}
}
