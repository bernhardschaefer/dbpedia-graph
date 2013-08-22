package de.unima.dws.dbpediagraph.graphdb.algorithms;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;

import de.unima.dws.dbpediagraph.graphdb.GraphConfig;
import de.unima.dws.dbpediagraph.graphdb.GraphHelper;
import de.unima.dws.dbpediagraph.graphdb.GraphProvider;
import de.unima.dws.dbpediagraph.graphdb.filter.DefaultEdgeFilter;
import de.unima.dws.dbpediagraph.graphdb.filter.EdgeFilter;
import de.unima.dws.dbpediagraph.graphdb.util.GraphPrinter;

public class SubgraphConstruction {
	private static final Logger logger = LoggerFactory.getLogger(SubgraphConstruction.class);
	private static final int DEFAULT_MAX_DEPTH = 5;

	public static void main(String[] args) {
		Graph graph = GraphProvider.getInstance().getGraph();

		SubgraphConstruction sc = new SubgraphConstruction(graph);

		Collection<Vertex> vertices = GraphHelper.getTestVertices(graph);
		long startTime = System.currentTimeMillis();
		Graph subGraph = sc.createSubgraphNaive(vertices);
		logger.info("Total time for creating subgraph: {} sec.", (System.currentTimeMillis() - startTime) / 1000.0);
		GraphPrinter.printGraphStatistics(subGraph);

		graph.shutdown();
	}

	private final LimitedDFS searchAlgorithm;

	public SubgraphConstruction(Graph graph) {
		this(graph, DEFAULT_MAX_DEPTH);
	}

	public SubgraphConstruction(Graph graph, int maxDepth) {
		this(graph, maxDepth, DefaultEdgeFilter.class, Direction.BOTH);
	}

	public SubgraphConstruction(Graph graph, int maxDepth, Class<? extends EdgeFilter> edgeFilter, Direction direction) {
		searchAlgorithm = new LimitedDFS(graph, maxDepth, edgeFilter, direction);
	}

	public Graph createSubgraphNaive(Collection<Vertex> vertices) {
		Graph subGraph = GraphProvider.getInstance().getNewEmptyGraph();

		GraphHelper.addVerticesByUrisOfVertices(subGraph, vertices);

		for (Vertex start : vertices) {
			for (Vertex end : vertices) {
				if (!start.equals(end)) {
					long startTime = System.currentTimeMillis();

					List<Edge> path = searchAlgorithm.findPathToTarget(start, end);

					long duration = System.currentTimeMillis() - startTime;
					logger.info("Path length {} from {} to {} in {} ms", path.size(),
							start.getProperty(GraphConfig.URI_PROPERTY), end.getProperty(GraphConfig.URI_PROPERTY),
							duration);

					if (!path.isEmpty()) {
						GraphHelper.addNodeAndEdgesIfNonExistent(subGraph, path);

						logger.info(GraphPrinter.toStringPath(path, start, end));
					}
					logger.info("");
				}
			}
		}

		return subGraph;
	}

	/**
	 * 
	 * @param vertices
	 *            the list contains the concatenations of all senses (e.g.
	 *            drink1, ... , drink5; milk1, ..., milk4)for each content word
	 *            (e.g. drink, milk),
	 */
	public Graph createSubgraphNavigli(List<Vertex> senses) {
		// problem is about finding a vertex-induced subgraph
		// (http://mathworld.wolfram.com/Vertex-InducedSubgraph.html)
		// (http://www.edmath.org/MATtours/discrete/concepts/csubgr.html)

		// V = vertices
		// List<Vertex> vertices = senses;
		// E = {}
		// List<Edge> edges = new LinkedList<Edge>();

		// perform a DFS from the vertex vertices(0) until we reach another
		// vertex from vertices(1-n).

		// add all edges and vertices on the path from both vertices
		// V = V.append(List<Vertex> path)
		// E = E.append(List<Edge> path)

		// backtrack to vertex before new vertex was discovered (path(n-2))

		return new TinkerGraph();
	}

}
