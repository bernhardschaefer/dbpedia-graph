package de.unima.dws.dbpediagraph.graphdb.subgraph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
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

import de.unima.dws.dbpediagraph.graphdb.GraphConfig;
import de.unima.dws.dbpediagraph.graphdb.GraphProvider;
import de.unima.dws.dbpediagraph.graphdb.GraphUtil;
import de.unima.dws.dbpediagraph.graphdb.util.GraphPrinter;

public class SubgraphConstructionNavigli extends TraversalAlgorithm implements SubgraphConstruction {
	private static final Logger logger = LoggerFactory.getLogger(SubgraphConstructionNavigli.class);

	public SubgraphConstructionNavigli(Graph graph) {
		super(graph);
	}

	private void checkValidSenses(Collection<Vertex> senses) {
		if (senses == null) {
			throw new NullPointerException("The senses collection cannot be null.");
		}
		if (senses.size() == 0) {
			throw new IllegalArgumentException("The senses collection cannot be empty.");
		}
		for (Vertex v : senses) {
			if (graph.getVertex(v.getId()) == null) {
				throw new IllegalArgumentException(String.format(
						"The vertex vid:%s uri:%s does not belong to this graph.", v.getId(),
						v.getProperty(GraphConfig.URI_PROPERTY)));
			}
		}
	}

	@Override
	public Graph createSubgraph(Collection<Vertex> senses) {
		checkValidSenses(senses);
		long startTime = System.currentTimeMillis();

		// initialize
		// V = vertices
		// E = {}
		Graph subGraph = GraphProvider.getInstance().getNewEmptyGraph();
		GraphUtil.addVerticesByUrisOfVertices(subGraph, senses);

		Deque<Vertex> dfsStack = new ArrayDeque<>();
		dfsStack.addAll(senses);
		Set<Vertex> visited = new HashSet<>();
		while (!dfsStack.isEmpty()) {
			Vertex v = dfsStack.pop();

			Collection<Vertex> otherSenses = null;
			if (senses.contains(v)) {
				// if the current vertex is a sense, we don't want to find a
				// path to itself
				otherSenses = new ArrayList<Vertex>(senses);
				otherSenses.remove(v);
			} else {
				otherSenses = senses;
			}

			// perform a DFS from the vertex vertices(0) until we reach another
			// vertex from vertices(1-n).
			List<Edge> path = performDepthFirstSearch(v, visited, otherSenses);

			if (path != null) {
				// add all edges and vertices on the path from both vertices
				// V = V.append(List<Vertex> path)
				// E = E.append(List<Edge> path)
				GraphUtil.addNodeAndEdgesIfNonExistent(subGraph, path);

				// add vertex before new vertex was discovered (path(n-2)) to
				// stack to enable backtracking and a new search starting from
				// this vertex
				dfsStack.addFirst(path.get(path.size() - 1).getVertex(Direction.OUT));
			}

		}

		logger.info("Total time for creating subgraph: {} sec.", (System.currentTimeMillis() - startTime) / 1000.0);
		return subGraph;

	}

	/**
	 * Perform a limited depth-first-search searching for other senses.
	 * 
	 * @param sense
	 *            the starting vertex
	 * @param visited
	 *            the vertex that have already been visited by other DFS
	 *            iterations
	 * @param otherSenses
	 *            the other senses as possible target nodes.
	 * @return the found path
	 */
	private List<Edge> performDepthFirstSearch(Vertex sense, Set<Vertex> visited, Collection<Vertex> otherSenses) {
		logger.info("From Sense: {}", sense.getProperty(GraphConfig.URI_PROPERTY));
		long startTime = System.currentTimeMillis();
		int visitedBefore = visited.size();

		Stack<Vertex> stack = new Stack<Vertex>();
		// track the path we used
		// stores the edge that have been traversed to reach the vertex
		Map<Vertex, Edge> previousMap = new HashMap<Vertex, Edge>();
		Map<Vertex, Integer> vertexDepth = new HashMap<Vertex, Integer>();

		Vertex foundSense = null;

		vertexDepth.put(sense, 0);

		stack.add(sense);
		visited.add(sense);
		while (!stack.isEmpty()) {
			Vertex next = stack.pop();
			if (otherSenses.contains(next)) {
				foundSense = next;
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
				if (!visited.contains(child)) {
					previousMap.put(child, edge);
					visited.add(child);
					stack.add(child);

					vertexDepth.put(child, depthNext + 1);
				}
			}
		}

		List<Edge> path = null;
		if (foundSense != null) {
			path = GraphUtil.getPathFromTraversalMap(sense, foundSense, previousMap);
			logger.info(GraphPrinter.toStringPath(path, sense, foundSense));
		} else {
			path = new ArrayList<Edge>();
		}

		long duration = System.currentTimeMillis() - startTime;
		logger.info("Path length: {} Traversed Nodes: {} Duration: {} ms ", path.size(),
				visited.size() - visitedBefore, duration);
		logger.info("");
		return path;
	}

}
