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
class SubgraphConstructionNavigliNew implements SubgraphConstruction {
	private static final Logger logger = LoggerFactory.getLogger(SubgraphConstructionNavigliNew.class);

	private Graph graph;

	private final SubgraphConstructionSettings settings;

	public SubgraphConstructionNavigliNew() {
		settings = SubgraphConstructionSettings.getDefault();
	}

	public SubgraphConstructionNavigliNew(Graph graph) {
		this(graph, SubgraphConstructionSettings.getDefault());
	}

	public SubgraphConstructionNavigliNew(Graph graph, SubgraphConstructionSettings settings) {
		this.graph = graph;
		this.settings = settings;
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

	private void checkValidWordsSenses(Collection<Collection<Vertex>> wordsSenses) {
		if (wordsSenses == null) {
			throw new NullPointerException("The senses collection cannot be null.");
		}
		if (wordsSenses.size() == 0) {
			throw new IllegalArgumentException("The senses collection cannot be empty.");
		}
		for (Collection<Vertex> senses : wordsSenses) {
			checkValidSenses(senses);
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
		checkValidWordsSenses(wordsSenses);
		long startTime = System.currentTimeMillis();

		// initialize
		// V = senses
		Set<Vertex> vertices = new HashSet<>();
		// E = {}
		Set<Edge> edges = new HashSet<>();

		Collection<Vertex> allSenses = CollectionUtils.combine(wordsSenses);
		for (Collection<Vertex> senses : wordsSenses) {
			Collection<Vertex> otherSenses = CollectionUtils.removeAll(allSenses, senses);
			for (Vertex v : senses) {
				// perform a DFS for the sense
				performDepthFirstSearch(v, otherSenses, vertices, edges);
			}
		}

		// build sub graph based on V and E
		Graph subGraph = GraphProvider.newInMemoryGraph();
		GraphUtil.addVerticesByUrisOfVertices(subGraph, allSenses);
		GraphUtil.addNodeAndEdgesIfNonExistent(subGraph, edges);

		logger.info("Total time for creating subgraph: {} sec.", (System.currentTimeMillis() - startTime) / 1000.0);
		return subGraph;
	}

	@Override
	public Graph getGraph() {
		return graph;
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
	private void performDepthFirstSearch(Vertex start, Collection<Vertex> otherSenses, Set<Vertex> vertices,
			Set<Edge> edges) {
		logger.info("");
		logger.info("DFS starting point: vid: {} uri: {}", start.getId(), start.getProperty(GraphConfig.URI_PROPERTY));

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

			// check limit
			int depthNext = vertexDepth.get(next);
			if (depthNext > settings.maxDistance) {
				// logger.debug("vid: {} uri: {} out of limit", next.getId(),
				// next.getProperty(GraphConfig.URI_PROPERTY));
				continue;
			}

			if (otherSenses.contains(next)) { // we found a sense of another word
				processFoundPath(start, next, vertices, edges, previousMap);
			}
			if (vertices.contains(next) && !otherSenses.contains(next)) {
				// special case: we have a child belongs to a path already
				processFoundPath(start, next, vertices, edges, previousMap);
				// continue since we don't want to further explore on this vertex
				continue;
			}

			for (Edge edge : next.getEdges(settings.direction)) {
				if (!settings.edgeFilter.isValidEdge(edge)) {
					// check if edge is relevant for subgraph
					continue;
				}
				Vertex child = GraphUtil.getOtherVertex(edge, next);
				if (!visited.contains(child) || vertices.contains(child)) {
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

	@Override
	public void setGraph(Graph graph) {
		this.graph = graph;
	}

}
