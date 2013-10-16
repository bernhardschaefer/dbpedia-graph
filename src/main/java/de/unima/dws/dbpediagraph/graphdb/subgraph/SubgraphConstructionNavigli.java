package de.unima.dws.dbpediagraph.graphdb.subgraph;

import java.util.Collection;
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
import de.unima.dws.dbpediagraph.graphdb.filter.EdgeFilter;
import de.unima.dws.dbpediagraph.graphdb.util.CollectionUtils;
import de.unima.dws.dbpediagraph.graphdb.util.GraphPrinter;

/**
 * Construct subgraph based on algorithm described in paper by Navigli and Lapata (2010).
 * 
 * @see <a
 *      href="http://ieeexplore.ieee.org/xpls/abs_all.jsp?arnumber=4782967">http://ieeexplore.ieee.org/xpls/abs_all.jsp?arnumber=4782967</a>
 * @author Bernhard Schäfer
 * 
 */
// TODO this is currently not working; still needs to figured out how to implement for directed and undirected subgraph
// construction
class SubgraphConstructionNavigli extends TraversalAlgorithm implements SubgraphConstruction {
	private static final Logger logger = LoggerFactory.getLogger(SubgraphConstructionNavigli.class);

	public SubgraphConstructionNavigli(Graph graph) {
		super(graph);
	}

	public SubgraphConstructionNavigli(Graph graph, int maxDistance) {
		super(graph, maxDistance);
	}

	public SubgraphConstructionNavigli(Graph graph, int maxDistance, EdgeFilter edgeFilter, Direction direction) {
		super(graph, maxDistance, edgeFilter, direction);
	}

	private void addIntermediateNodes(List<Edge> path, Set<Vertex> vertices) {
		if (path.size() > 1) {
			List<Edge> intermedPath = path.subList(1, path.size());
			for (Edge e : intermedPath) {
				Vertex v = e.getVertex(Direction.OUT);
				vertices.add(v);
			}
		}
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
		// V = senses
		Set<Vertex> vertices = new HashSet<>();
		// E = {}
		Set<Edge> edges = new HashSet<>();

		for (Vertex v : senses) {
			// perform a DFS for the sense
			performDepthFirstSearch(v, senses, vertices, edges);
		}

		// build sub graph based on V and E
		Graph subGraph = GraphProvider.newInMemoryGraph();
		GraphUtil.addVerticesByUrisOfVertices(subGraph, senses);
		GraphUtil.addNodeAndEdgesIfNonExistent(subGraph, edges);

		logger.info("Total time for creating subgraph: {} sec.", (System.currentTimeMillis() - startTime) / 1000.0);
		return subGraph;

	}

	@Override
	public Graph createSubgraphFromSenses(Collection<Collection<Vertex>> wordsSenses) {
		return createSubgraph(CollectionUtils.combine(wordsSenses));
	}

	private Set<Edge> getConnectedUntraversedEdges(Vertex current, EdgeFilter edgeFilter, Direction direction,
			Map<Vertex, Integer> visitedVerticesDistance, Set<Vertex> vertices, Map<Vertex, Edge> previousMap) {
		Set<Edge> untraversedEdges = new HashSet<Edge>();

		edgeFilter.setIterator(current.getEdges(direction).iterator());
		for (Edge edge : edgeFilter) {
			// Vertex child = edge.getVertex(Direction.IN);
			// if (!visited.contains(child) || vertices.contains(child)) {
			// previous map edge is overwritten in case we find another path
			// TODO check if this behavior is problematic

			// 1. get vertex of edge that is not equal to next
			Vertex other = getOtherVertexOfEdge(current, edge);

			// 2. check if we just got from this vertex
			Edge last = previousMap.get(current);
			// last == null at the very beginning
			if (last != null
					&& (last.getVertex(Direction.IN).equals(other) || last.getVertex(Direction.OUT).equals(other))) {
				continue;
			}

			if (!visitedVerticesDistance.containsKey(other) || vertices.contains(other))
				untraversedEdges.add(edge);
		}
		return untraversedEdges;
	}

	public Vertex getOtherVertexOfEdge(Vertex v, Edge e) {
		Vertex out = e.getVertex(Direction.OUT);
		Vertex in = e.getVertex(Direction.IN);
		return out.equals(v) ? in : out;
	}

	/**
	 * Perform a limited depth-first-search searching for other senses.
	 * 
	 * @param start
	 *            the starting vertex
	 * @param visited
	 *            the vertex that have already been visited by other DFS iterations
	 * @param otherSenses
	 *            the other senses as possible target nodes.
	 * @return the found path
	 */
	private void performDepthFirstSearch(Vertex start, Collection<Vertex> senses, Set<Vertex> vertices, Set<Edge> edges) {
		logger.info("");
		logger.info("DFS starting point: vid: {} uri: {}", start.getId(), start.getProperty(GraphConfig.URI_PROPERTY));

		Stack<Vertex> stack = new Stack<>();
		// track the path we used
		// stores the edge that have been traversed to reach the vertex
		Map<Vertex, Edge> previousMap = new HashMap<>();
		Set<Edge> visitedEdges = new HashSet<>();
		Map<Vertex, Integer> visitedVertexDistance = new HashMap<>();

		visitedVertexDistance.put(start, 0);
		stack.add(start);

		while (!stack.isEmpty()) {
			Vertex next = stack.pop();

			// check limit
			int depthNext = visitedVertexDistance.get(next);
			if (depthNext > maxDistance) {
				// logger.debug("vid: {} uri: {} out of limit", next.getId(),
				// next.getProperty(GraphConfig.URI_PROPERTY));
				continue;
			}

			if (!next.equals(start) && senses.contains(next)) {
				processFoundPath(start, next, vertices, edges, previousMap);
			}
			if (vertices.contains(next) && !senses.contains(next)) {
				// special case: we have a child belongs to a path already
				processFoundPath(start, next, vertices, edges, previousMap);
				// continue since we don't want to further explore on this vertex
				continue;
			}

			Set<Edge> untraversedNeighbors = getConnectedUntraversedEdges(next, edgeFilter, direction,
					visitedVertexDistance, vertices, previousMap);
			for (Edge edge : untraversedNeighbors) {
				Vertex other = getOtherVertexOfEdge(next, edge);
				previousMap.put(other, edge);
				stack.add(other);

				visitedVertexDistance.put(other, depthNext + 1);
				visitedEdges.add(edge);
			}
		}

	}

	private void processFoundPath(Vertex start, Vertex end, Set<Vertex> vertices, Set<Edge> edges,
			Map<Vertex, Edge> previousMap) {
		// found path v,v1,...,vk,v'
		List<Edge> path = GraphUtil.getPathFromTraversalMap(start, end, previousMap);

		// add all intermediate nodes and edges on the path

		// V = V.append(v1,...,vk)
		addIntermediateNodes(path, vertices);

		// E = E.append({{v,v1},...,{vk,v'}})
		edges.addAll(path);

		logger.info("Found sense vid: {} uri: {}", end.getId(), end.getProperty(GraphConfig.URI_PROPERTY));
		logger.info(GraphPrinter.toStringPath(path, start, end));

	}

}
