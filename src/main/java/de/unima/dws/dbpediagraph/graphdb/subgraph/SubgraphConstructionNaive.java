package de.unima.dws.dbpediagraph.graphdb.subgraph;

import java.util.Collection;
import java.util.List;

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
import de.unima.dws.dbpediagraph.graphdb.util.GraphPrinter;

/**
 * Non-optimized {@link SubgraphConstruction} implementation. Performs a limited
 * shortest-path search between all combinations of input vertices.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class SubgraphConstructionNaive implements SubgraphConstruction {
	private static final Logger logger = LoggerFactory.getLogger(SubgraphConstructionNaive.class);

	public static void main(String[] args) {
		Graph graph = GraphProvider.getInstance().getGraph();

		SubgraphConstructionNaive sc = new SubgraphConstructionNaive(graph);

		Collection<Vertex> vertices = GraphUtil.getTestVertices(graph);
		long startTime = System.currentTimeMillis();
		Graph subGraph = sc.createSubgraph(vertices);
		logger.info("Total time for creating subgraph: {} sec.", (System.currentTimeMillis() - startTime) / 1000.0);
		GraphPrinter.printGraphStatistics(subGraph);

		graph.shutdown();
	}

	private final LimitedDFS searchAlgorithm;

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
		Graph subGraph = GraphProvider.getInstance().getNewEmptyGraph();

		GraphUtil.addVerticesByUrisOfVertices(subGraph, senses);

		for (Vertex start : senses) {
			for (Vertex end : senses) {
				if (!start.equals(end)) {
					long startTime = System.currentTimeMillis();

					List<Edge> path = searchAlgorithm.findPathToTarget(start, end);

					long duration = System.currentTimeMillis() - startTime;
					logger.info("Path length {} from {} to {} in {} ms", path.size(),
							start.getProperty(GraphConfig.URI_PROPERTY), end.getProperty(GraphConfig.URI_PROPERTY),
							duration);

					if (!path.isEmpty()) {
						GraphUtil.addNodeAndEdgesIfNonExistent(subGraph, path);

						logger.info(GraphPrinter.toStringPath(path, start, end));
					}
					logger.info("");
				}
			}
		}

		return subGraph;
	}

}
