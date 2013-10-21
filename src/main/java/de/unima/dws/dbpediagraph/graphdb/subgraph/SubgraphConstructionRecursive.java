package de.unima.dws.dbpediagraph.graphdb.subgraph;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.GraphConfig;
import de.unima.dws.dbpediagraph.graphdb.GraphFactory;
import de.unima.dws.dbpediagraph.graphdb.Graphs;
import de.unima.dws.dbpediagraph.graphdb.util.CollectionUtils;

/**
 * Construct subgraph based on algorithm described in paper by Navigli and Lapata (2010). NOTE: This implementation only
 * finds shortest paths, and not all paths between two sense vertices.
 * 
 * @see <a
 *      href="http://ieeexplore.ieee.org/xpls/abs_all.jsp?arnumber=4782967">http://ieeexplore.ieee.org/xpls/abs_all.jsp?arnumber=4782967</a>
 * @author Bernhard Schäfer
 * 
 */
class SubgraphConstructionRecursive implements SubgraphConstruction {
	private static final Logger logger = LoggerFactory.getLogger(SubgraphConstructionIterative.class);

	private final Graph graph;
	private final SubgraphConstructionSettings settings;

	private int traversedNodes;

	public SubgraphConstructionRecursive(Graph graph, SubgraphConstructionSettings settings) {
		this.graph = graph;
		this.settings = settings;
	}

	@Override
	public Graph createSubgraph(Collection<Collection<Vertex>> wordsSenses) {
		long startTime = System.currentTimeMillis();

		SubgraphConstructions.checkValidWordsSenses(graph, wordsSenses);
		traversedNodes = 0;

		Collection<Vertex> allSenses = CollectionUtils.combine(wordsSenses);

		// initialize subgraph with all senses of all words
		Graph subGraph = GraphFactory.newInMemoryGraph();
		Graphs.addVerticesByUrisOfVertices(subGraph, allSenses);

		// perform a DFS for each sense trying to find path to senses of other words
		for (Collection<Vertex> senses : wordsSenses) {
			Collection<Vertex> otherSenses = CollectionUtils.removeAll(allSenses, senses);
			for (Vertex start : senses) {
				logger.info("Starting DFS with vid: {}, uri: {}", start.getId(),
						start.getProperty(GraphConfig.URI_PROPERTY));
				dfs(new Path(start), otherSenses, subGraph);
				// dfs(start, new ArrayList<Edge>(), otherSenses, subGraph);
			}
		}

		SubgraphConstructions.logSubgraphConstructionStats(logger, getClass(), subGraph, startTime, traversedNodes,
				settings.maxDistance);

		return subGraph;
	}

	// protected void dfs(Vertex current, List<Edge> path, Collection<Vertex> targets, Graph subGraph) {
	// traversedNodes++;
	//
	// // check limit
	// if (path.size() > settings.maxDistance) {
	// return;
	// }
	//
	// // check if target node
	// if (targets.contains(current)) {
	// SubgraphConstructions.addPathToSubGraph(current, path, subGraph);
	// }
	//
	// // explore further
	// for (Edge edge : current.getEdges(Direction.OUT)) {
	// Vertex child = edge.getVertex(Direction.IN);
	// if (!Graphs.isNodeOnPath(child, path)) { // this is slow (linear time)
	// List<Edge> newPath = new ArrayList<>(path);
	// newPath.add(edge);
	// dfs(child, newPath, targets, subGraph);
	// }
	// }
	// }

	protected void dfs(Path path, Collection<Vertex> targets, Graph subGraph) {
		traversedNodes++;

		Vertex current = path.getLast();

		// check limit
		if (path.getEdges().size() > settings.maxDistance) {
			return;
		}

		// check if target node
		if (targets.contains(current)) {
			// TODO enable undirected direction
			SubgraphConstructions.addPathToSubGraph(current, path, subGraph, settings.graphType);
		}

		// explore further
		for (Edge edge : Graphs.getUntraversedConnectedEdges(current, path.getEdges(), settings.graphType)) {
			Vertex child = Graphs.getOppositeVertex(edge, current);
			if (!path.getVertices().contains(child)) {
				Path newPath = Path.newHop(path, edge, child);
				dfs(newPath, targets, subGraph);
			}
		}
	}
}
