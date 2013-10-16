package de.unima.dws.dbpediagraph.graphdb.subgraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.furnace.algorithms.graphcentric.searching.DepthFirstAlgorithm;
import com.tinkerpop.furnace.algorithms.graphcentric.searching.SearchAlgorithm;

import de.unima.dws.dbpediagraph.graphdb.GraphConfig;
import de.unima.dws.dbpediagraph.graphdb.GraphUtil;
import de.unima.dws.dbpediagraph.graphdb.filter.EdgeFilter;
import de.unima.dws.dbpediagraph.graphdb.util.GraphPrinter;

/**
 * Depth-first search with a depth limit. This class is mostly based on {@link DepthFirstAlgorithm}, but has been
 * modified to support a maxDepth constraint and fixed a {@link NullPointerException} bug that occurred when no path was
 * found.
 * 
 * @author Bernhard Schäfer
 * 
 */
class LimitedDFS extends TraversalAlgorithm implements SearchAlgorithm {
	private static final Logger logger = LoggerFactory.getLogger(LimitedDFS.class);

	public LimitedDFS(Graph graph) {
		super(graph);
	}

	public LimitedDFS(Graph graph, int maxDistance) {
		super(graph, maxDistance);
	}

	public LimitedDFS(Graph graph, int maxDistance, EdgeFilter edgeFilter, Direction direction) {
		super(graph, maxDistance, edgeFilter, direction);
	}

	private void checkValidVertices(Vertex start, Vertex target) {
		if (start == null) {
			throw new NullPointerException("Start cannot be null");
		}
		if (target == null) {
			throw new NullPointerException("Target cannot be null");
		}

		start = graph.getVertex(start.getId());
		if (start == null) {
			throw new IllegalStateException("Start vertex does not belong to this graph.");
		}
		target = graph.getVertex(target.getId());
		if (target == null) {
			throw new IllegalStateException("Target vertex does not belong to this graph.");
		}
	}

	@Override
	public List<Edge> findPathToTarget(Vertex start, Vertex target) {
		checkValidVertices(start, target);
		return performDepthFirstSearch(start, target);
	}

	private List<Edge> performDepthFirstSearch(Vertex start, Vertex end) {
		logger.info("From: {} To: {}", start.getProperty(GraphConfig.URI_PROPERTY),
				end.getProperty(GraphConfig.URI_PROPERTY));
		long startTime = System.currentTimeMillis();

		if (start.equals(end)) {
			return new LinkedList<Edge>();
		}

		Stack<Vertex> stack = new Stack<Vertex>();
		Set<Vertex> visitedSet = new HashSet<Vertex>();
		// track the path we used
		// stores the edge that have been traversed to reach the vertex
		Map<Vertex, Edge> previousMap = new HashMap<Vertex, Edge>();
		Map<Vertex, Integer> vertexDepth = new HashMap<Vertex, Integer>();

		boolean foundPath = false;

		vertexDepth.put(start, 0);

		stack.add(start);
		visitedSet.add(start);
		while (!stack.isEmpty()) {
			Vertex next = stack.pop();
			if (end.equals(next)) {
				foundPath = true;
				break;
			}

			// check limit
			int depthNext = vertexDepth.get(next);
			if (depthNext > maxDistance) {
				logger.debug("vid: {} uri: {} out of limit", next.getId(), next.getProperty(GraphConfig.URI_PROPERTY));
				continue;
			}

			edgeFilter.setIterator(next.getEdges(direction).iterator());
			for (Edge edge : edgeFilter) {
				Vertex child = edge.getVertex(Direction.IN);
				if (!visitedSet.contains(child)) {
					previousMap.put(child, edge);
					visitedSet.add(child);
					stack.add(child);

					vertexDepth.put(child, depthNext + 1);
				}
			}
		}

		List<Edge> path = null;
		if (foundPath) {
			path = GraphUtil.getPathFromTraversalMap(start, end, previousMap);
			logger.info(GraphPrinter.toStringPath(path, start, end));
		} else {
			path = new ArrayList<Edge>();
		}

		long duration = System.currentTimeMillis() - startTime;
		logger.info("Path length: {} Traversed Nodes: {} Duration: {} ms ", path.size(), visitedSet.size(), duration);
		logger.info("");
		return path;
	}

}
