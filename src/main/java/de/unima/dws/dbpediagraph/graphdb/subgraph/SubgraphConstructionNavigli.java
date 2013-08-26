package de.unima.dws.dbpediagraph.graphdb.subgraph;

import java.util.*;

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

			Collection<Vertex> otherSenses = new ArrayList<Vertex>(senses);
			if (senses.contains(v)) {
				// if the current vertex is a sense, we don't want to find a
				// path to itself
				otherSenses.remove(v);
			}

			// perform a DFS from the vertex vertices(0) until we reach another
			// vertex from vertices(1-n).
			List<Edge> path = performDepthFirstSearch(v, visited, otherSenses);

			if (path != null && !path.isEmpty()) {
				// add all edges and vertices on the path from both vertices
				// V = V.append(List<Vertex> path)
				// E = E.append(List<Edge> path)
				GraphUtil.addNodeAndEdgesIfNonExistent(subGraph, path);

				// add intermediate vertices on path
				if (path.size() > 1) {
					List<Edge> intermedPath = path.subList(1, path.size());
					for (Edge e : intermedPath) {
						Vertex backtrack = e.getVertex(Direction.OUT);
						dfsStack.addFirst(backtrack);
					}
				}
			}

		}

		logger.info("Total time for creating subgraph: {} sec.", (System.currentTimeMillis() - startTime) / 1000.0);
		return subGraph;

	}

	/**
	 * Perform a limited depth-first-search searching for other senses.
	 * 
	 * @param start
	 *            the starting vertex
	 * @param visited
	 *            the vertex that have already been visited by other DFS
	 *            iterations
	 * @param otherSenses
	 *            the other senses as possible target nodes.
	 * @return the found path
	 */
	private List<Edge> performDepthFirstSearch(Vertex start, Set<Vertex> visited, Collection<Vertex> otherSenses) {
		logger.info("DFS starting point: vid: {} uri: {}", start.getId(), start.getProperty(GraphConfig.URI_PROPERTY));
		long startTime = System.currentTimeMillis();
		int visitedBefore = visited.size();

		Stack<Vertex> stack = new Stack<Vertex>();
		// track the path we used
		// stores the edge that have been traversed to reach the vertex
		Map<Vertex, Edge> previousMap = new HashMap<Vertex, Edge>();
		Map<Vertex, Integer> vertexDepth = new HashMap<Vertex, Integer>();

		Vertex foundSense = null;

		vertexDepth.put(start, 0);

		stack.add(start);
		visited.add(start);
		while (!stack.isEmpty()) {
			Vertex next = stack.pop();
			if (otherSenses.contains(next)) {
				foundSense = next;
				break;
			}

			// check limit
			int depthNext = vertexDepth.get(next);
			if (depthNext > maxDistance) {
				// logger.debug("vid: {} uri: {} out of limit", next.getId(),
				// next.getProperty(GraphConfig.URI_PROPERTY));
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
			path = GraphUtil.getPathFromTraversalMap(start, foundSense, previousMap);
			logger.info("Found sense vid: {} uri: {}", foundSense.getId(),
					foundSense.getProperty(GraphConfig.URI_PROPERTY));
			logger.info(GraphPrinter.toStringPath(path, start, foundSense));
		}

		int pathSize = path != null ? path.size() : -1;
		long duration = System.currentTimeMillis() - startTime;
		logger.info("Path length: {} Traversed Nodes: {} Duration: {} ms ", pathSize, visited.size() - visitedBefore,
				duration);
		logger.info("");
		return path;
	}

}
