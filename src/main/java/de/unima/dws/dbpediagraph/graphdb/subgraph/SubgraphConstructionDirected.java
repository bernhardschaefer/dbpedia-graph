package de.unima.dws.dbpediagraph.graphdb.subgraph;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

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

/**
 * Construct subgraph based on algorithm described in paper by Navigli and Lapata (2010). NOTE: This implementation only
 * finds shortest paths, and not all paths between two sense vertices.
 * 
 * @see <a
 *      href="http://ieeexplore.ieee.org/xpls/abs_all.jsp?arnumber=4782967">http://ieeexplore.ieee.org/xpls/abs_all.jsp?arnumber=4782967</a>
 * @author Bernhard Schäfer
 * 
 */
class SubgraphConstructionDirected implements SubgraphConstruction {
	private static final Logger logger = LoggerFactory.getLogger(SubgraphConstructionDirected.class);

	private Graph graph;

	private final SubgraphConstructionSettings settings;

	private int traversedNodes;

	public SubgraphConstructionDirected(Graph graph, SubgraphConstructionSettings settings) {
		this.graph = graph;
		this.settings = settings;
	}

	public SubgraphConstructionDirected(SubgraphConstructionSettings settings) {
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
				performDepthFirstSearch(start, otherSenses, subGraph);
			}
		}

		logger.info("subgraph construction. time {} sec., traversed nodes: {}, maxDepth: {}",
				(System.currentTimeMillis() - startTime) / 1000.0, traversedNodes, settings.maxDistance);
		return subGraph;
	}

	@Override
	public Graph getGraph() {
		return graph;
	}

	/**
	 * Performs a DFS starting at the start vertex. The goal is to find all paths within the max distance to the other
	 * provided senses. Found paths are inserted into the subgraph.
	 * 
	 * @param start
	 *            the vertex the DFS starts with
	 * @param otherSenses
	 *            the target senses
	 * @param subGraph
	 *            the subgraph where the paths are inserted to
	 */
	private void performDepthFirstSearch(Vertex start, Collection<Vertex> otherSenses, Graph subGraph) {
		logger.debug("");
		logger.debug("DFS starting point: vid: {} uri: {}", start.getId(), start.getProperty(GraphConfig.URI_PROPERTY));

		Stack<Vertex> stack = new Stack<>();
		// track the path we used
		// stores the edge that have been traversed to reach the vertex
		Map<Vertex, Edge> previousMap = new HashMap<>();
		Set<Vertex> visited = new HashSet<>();
		Map<Vertex, Integer> vertexDepth = new HashMap<>();

		vertexDepth.put(start, 0);

		stack.add(start);
		visited.add(start);
		while (!stack.isEmpty()) {
			Vertex next = stack.pop();
			traversedNodes++;

			// check limit
			int depthNext = vertexDepth.get(next);
			if (depthNext > settings.maxDistance) {
				continue;
			}

			if (otherSenses.contains(next)) { // we found a sense of another word
				SubgraphConstructionHelper.processFoundPath(start, next, previousMap, subGraph);
			}

			for (Edge edge : next.getEdges(Direction.OUT)) {
				Vertex child = edge.getVertex(Direction.IN);
				if (!visited.contains(child) || GraphUtil.isVertexInGraph(child, subGraph)) {
					// previous map edge is overwritten in case we find another path
					// TODO check if this behavior is problematic
					previousMap.put(child, edge);
					visited.add(child);
					stack.add(child);
					vertexDepth.put(child, depthNext + 1);
				}
			}
		}

	}

	@Override
	public void setGraph(Graph graph) {
		this.graph = graph;
	}

}
