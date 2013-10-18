package de.unima.dws.dbpediagraph.graphdb.subgraph;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.GraphConfig;
import de.unima.dws.dbpediagraph.graphdb.GraphProvider;
import de.unima.dws.dbpediagraph.graphdb.Graphs;
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
class SubgraphConstructionDirectedIterative implements SubgraphConstruction {
	private static final Logger logger = LoggerFactory.getLogger(SubgraphConstructionDirectedIterative.class);

	private final Graph graph;
	private final SubgraphConstructionSettings settings;

	private int traversedNodes;

	public SubgraphConstructionDirectedIterative(Graph graph, SubgraphConstructionSettings settings) {
		this.graph = graph;
		this.settings = settings;
	}

	@Override
	public Graph createSubgraph(Collection<Collection<Vertex>> wordsSenses) {
		SubgraphConstructionHelper.checkValidWordsSenses(graph, wordsSenses);
		long startTime = System.currentTimeMillis();

		Collection<Vertex> allSenses = CollectionUtils.combine(wordsSenses);

		// initialize subgraph with all senses of all words
		Graph subGraph = GraphProvider.newInMemoryGraph();
		Graphs.addVerticesByUrisOfVertices(subGraph, allSenses);

		// perform a DFS for each sense trying to find path to senses of other words
		for (Collection<Vertex> senses : wordsSenses) {
			Collection<Vertex> otherSenses = CollectionUtils.removeAll(allSenses, senses);
			for (Vertex start : senses) {
				performDepthFirstSearch(start, otherSenses, subGraph);
			}
		}

		GraphPrinter.logSubgraphConstructionStats(logger, getClass(), subGraph, startTime, traversedNodes,
				settings.maxDistance);

		return subGraph;
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
	private void performDepthFirstSearch(Vertex start, Collection<Vertex> targets, Graph subGraph) {
		logger.debug("DFS starting point: vid: {} uri: {}", start.getId(), start.getProperty(GraphConfig.URI_PROPERTY));

		Deque<Path> stack = new ArrayDeque<>();
		stack.push(new Path(start));
		while (!stack.isEmpty()) {
			traversedNodes++;

			Path path = stack.pop();
			Vertex current = path.getLastVertex();

			// check limit
			if (path.getEdges().size() > settings.maxDistance) {
				continue;
			}

			// check if target node
			if (targets.contains(current)) {
				Graphs.addPathToSubGraph(current, path, subGraph);
			}

			// explore further
			for (Edge edge : current.getEdges(Direction.OUT)) {
				Vertex child = edge.getVertex(Direction.IN);
				if (!path.getVertices().contains(child)) {
					Path newPath = new Path(path);
					newPath.getVertices().add(child);
					newPath.getEdges().add(edge);
					stack.push(newPath);
				}
			}
		}
	}
}
