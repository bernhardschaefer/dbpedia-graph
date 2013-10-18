package de.unima.dws.dbpediagraph.graphdb.subgraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.GraphConfig;
import de.unima.dws.dbpediagraph.graphdb.GraphProvider;
import de.unima.dws.dbpediagraph.graphdb.GraphUtil;
import de.unima.dws.dbpediagraph.graphdb.util.CollectionUtils;
import de.unima.dws.dbpediagraph.graphdb.util.GraphPrinter;

/**
 * Construct subgraph based on algorithm described in paper by Navigli and Lapata (2010). NOTE: This implementation only
 * finds shortest paths, and not all paths between two sense vertices.
 * 
 * @see <a
 *      href="http://ieeexplore.ieee.org/xpls/abs_all.jsp?arnumber=4782967">http://ieeexplore.ieee.org/xpls/abs_all.jsp?arnumber=4782967</a>
 * @author Bernhard Schäfer
 * 
 */
class SubgraphConstructionRec implements SubgraphConstruction {
	private static final Logger logger = LoggerFactory.getLogger(SubgraphConstructionDirected.class);

	private static void addPathToSubGraph(Vertex current, List<Edge> path, Graph subGraph) {
		Vertex start = path.get(0).getVertex(Direction.OUT);
		logger.debug("Found sense vid: {} uri: {}", current.getId(), current.getProperty(GraphConfig.URI_PROPERTY));
		logger.debug(GraphPrinter.toStringPath(path, start, current));
		GraphUtil.addNodeAndEdgesIfNonExistent(subGraph, path);
	}

	private static void addPathToSubGraph(Vertex current, Path<Vertex, Edge> path, Graph subGraph) {
		addPathToSubGraph(current, path.getEdges(), subGraph);
	}

	private Graph graph;

	private final SubgraphConstructionSettings settings;

	private int traversedNodes;

	public SubgraphConstructionRec(Graph graph, SubgraphConstructionSettings settings) {
		this.graph = graph;
		this.settings = settings;
	}

	public SubgraphConstructionRec(SubgraphConstructionSettings settings) {
		this.settings = settings;
	}

	@Override
	public Graph createSubgraph(Collection<Collection<Vertex>> wordsSenses) {
		SubgraphConstructionHelper.checkValidWordsSenses(graph, wordsSenses);
		long startTime = System.currentTimeMillis();

		Collection<Vertex> allSenses = CollectionUtils.combine(wordsSenses);

		// initialize subgraph with all senses of all words
		Graph subGraph = GraphProvider.newInMemoryGraph();
		GraphUtil.addVerticesByUrisOfVertices(subGraph, allSenses);

		// perform a DFS for each sense trying to find path to senses of other words
		for (Collection<Vertex> senses : wordsSenses) {
			Collection<Vertex> otherSenses = CollectionUtils.removeAll(allSenses, senses);
			for (Vertex start : senses) {
				logger.info("Starting DFS with vid: {}, uri: {}", start.getId(),
						start.getProperty(GraphConfig.URI_PROPERTY));
				dfs(start, new Path<Vertex, Edge>(), otherSenses, subGraph);
				// dfs(start, new ArrayList<Edge>(), otherSenses, subGraph);
			}
		}

		logger.info("subgraph construction. time {} sec., traversed nodes: {}, maxDepth: {}",
				(System.currentTimeMillis() - startTime) / 1000.0, traversedNodes, settings.maxDistance);
		return subGraph;
	}

	protected void dfs(Vertex current, List<Edge> path, Collection<Vertex> targets, Graph subGraph) {
		traversedNodes++;

		// check limit
		if (path.size() > settings.maxDistance) {
			return;
		}

		// check if target node
		if (targets.contains(current)) {
			addPathToSubGraph(current, path, subGraph);
		}

		// explore further
		for (Edge edge : current.getEdges(Direction.OUT)) {
			Vertex child = edge.getVertex(Direction.IN);
			if (!GraphUtil.isNodeOnPath(child, path)) { // this is slow (linear time)
				List<Edge> newPath = new ArrayList<>(path);
				newPath.add(edge);
				dfs(child, newPath, targets, subGraph);
			}
		}
	}

	protected void dfs(Vertex current, Path<Vertex, Edge> path, Collection<Vertex> targets, Graph subGraph) {
		traversedNodes++;

		// check limit
		if (path.getEdges().size() > settings.maxDistance) {
			return;
		}

		// check if target node
		if (targets.contains(current)) {
			addPathToSubGraph(current, path, subGraph);
		}

		// explore further
		for (Edge edge : current.getEdges(Direction.OUT)) {
			Vertex child = edge.getVertex(Direction.IN);
			if (!path.getVertices().contains(child)) {
				Path<Vertex, Edge> newPath = new Path<>(path);
				newPath.getVertices().add(child);
				newPath.getEdges().add(edge);
				dfs(child, newPath, targets, subGraph);
			}
		}
	}

	@Override
	public Graph getGraph() {
		return graph;
	}

	@Override
	public void setGraph(Graph graph) {
		this.graph = graph;
	}

}
