package de.unima.dws.dbpediagraph.graphdb.subgraph;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.furnace.algorithms.graphcentric.searching.SearchAlgorithm;

import de.unima.dws.dbpediagraph.graphdb.GraphProvider;
import de.unima.dws.dbpediagraph.graphdb.GraphUtil;
import de.unima.dws.dbpediagraph.graphdb.filter.EdgeFilter;

/**
 * Non-optimized {@link SubgraphConstruction} implementation. Performs a limited shortest-path search between all
 * combinations of input vertices.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class SubgraphConstructionNaive implements SubgraphConstruction {
	private static final Logger logger = LoggerFactory.getLogger(SubgraphConstructionNaive.class);

	/**
	 * The search algorithm to use for traversing the graph and finding shortest paths.
	 */
	private final SearchAlgorithm searchAlgorithm;

	public SubgraphConstructionNaive(Graph graph) {
		searchAlgorithm = new LimitedDFS(graph);
	}

	public SubgraphConstructionNaive(Graph graph, int maxDepth) {
		searchAlgorithm = new LimitedDFS(graph, maxDepth);
	}

	public SubgraphConstructionNaive(Graph graph, int maxDepth, EdgeFilter edgeFilter, Direction direction) {
		searchAlgorithm = new LimitedDFS(graph, maxDepth, edgeFilter, direction);
	}

	@Override
	public Graph createSubgraph(Collection<Vertex> senses) {
		long startTime = System.currentTimeMillis();

		Graph subGraph = GraphProvider.getInstance().getNewEmptyGraph();

		GraphUtil.addVerticesByUrisOfVertices(subGraph, senses);

		for (Vertex start : senses) {
			for (Vertex end : senses) {
				if (!start.equals(end)) {
					List<Edge> path = searchAlgorithm.findPathToTarget(start, end);
					if (!path.isEmpty()) {
						GraphUtil.addNodeAndEdgesIfNonExistent(subGraph, path);
					}
				}
			}
		}

		logger.info("Total time for creating subgraph: {} sec.", (System.currentTimeMillis() - startTime) / 1000.0);
		return subGraph;
	}

}
