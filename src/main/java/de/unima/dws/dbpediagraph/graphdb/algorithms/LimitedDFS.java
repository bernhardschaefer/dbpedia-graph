package de.unima.dws.dbpediagraph.graphdb.algorithms;

import java.util.ArrayList;
import java.util.Collections;
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

/**
 * Depth-first search with a depth limit. This class is mostly based on
 * {@link DepthFirstAlgorithm}.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class LimitedDFS implements SearchAlgorithm {
	private static final Logger logger = LoggerFactory.getLogger(LimitedDFS.class);

	private final Graph graph;

	private final int limit;

	public LimitedDFS(Graph graph, int limit) {
		this.graph = graph;
		this.limit = limit;
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

	private List<Edge> getListFromPreviousMap(Vertex start, Vertex end, Map<Vertex, Edge> previousMap) {
		List<Edge> pathFromStartToEnd = new LinkedList<Edge>();
		Vertex previousVertex = end;
		while (!start.equals(previousVertex)) {
			Edge currentEdge = previousMap.get(previousVertex);
			pathFromStartToEnd.add(currentEdge);
			previousVertex = currentEdge.getVertex(Direction.OUT);
		}
		Collections.reverse(pathFromStartToEnd);
		return pathFromStartToEnd;
	}

	private List<Edge> performDepthFirstSearch(Vertex start, Vertex end) {
		if (start.equals(end)) {
			return new LinkedList<Edge>();
		}

		Stack<Vertex> stack = new Stack<Vertex>();
		Set<Vertex> visitedSet = new HashSet<Vertex>();
		// track the path we used
		// stores the edge that have been traversed to reach the vertex
		Map<Vertex, Edge> previousMap = new HashMap<Vertex, Edge>();
		Map<Vertex, Integer> vertexDepth = new HashMap<>();

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
			int limitNext = vertexDepth.get(next);
			if (limitNext > limit) {
				logger.debug("vid: {} uri: {} out of limit", next.getId(), next.getProperty(GraphConfig.URI_PROPERTY));
				continue;
			}

			for (Edge edge : next.getEdges(Direction.OUT)) {
				Vertex child = edge.getVertex(Direction.IN);
				if (!visitedSet.contains(child)) {
					previousMap.put(child, edge);
					visitedSet.add(child);
					stack.add(child);

					vertexDepth.put(child, limitNext + 1);
				}
			}
		}

		if (foundPath) {
			return getListFromPreviousMap(start, end, previousMap);
		} else {
			return new ArrayList<Edge>();
		}
	}

}
