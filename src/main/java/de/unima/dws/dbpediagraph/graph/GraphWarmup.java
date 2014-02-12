package de.unima.dws.dbpediagraph.graph;

import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.tinkerpop.blueprints.*;

import de.unima.dws.dbpediagraph.util.EnumUtils;

/**
 * Graph warm-up steps to load parts of the graph into RAM.
 * 
 * @author Bernhard Schäfer
 * 
 */
public enum GraphWarmup {
	TRAVERSE_VERTICES {
		@Override
		void warmup(Graph graph) {
			Stopwatch vertCountTime = Stopwatch.createStarted();
			int vertCount = Graphs.verticesCount(graph);
			logger.info("Traversed {} vertices in {}", vertCount, vertCountTime);
		}
	},
	TRAVERSE_EDGES {
		@Override
		void warmup(Graph graph) {
			Stopwatch edgesCountTime = Stopwatch.createStarted();
			int edgesCount = Graphs.edgesCount(graph);
			logger.info("Traversed {} edges in {}", edgesCount, edgesCountTime);
		}
	},
	TRAVERSE_VERTEX_URIS {
		@Override
		void warmup(Graph graph) {
			Stopwatch vertUriTime = Stopwatch.createStarted();
			for (Vertex v : graph.getVertices()) {
				String uri = Graphs.shortUriOfVertex(v);
				logger.trace("Found uri {}", uri); // prevent compiler optimization from removing previous call
			}
			logger.info("Traversed over all vertex uris in {}", vertUriTime);
		}
	},
	TRAVERSE_EDGE_URIS {
		@Override
		void warmup(Graph graph) {
			Stopwatch edgesUriTime = Stopwatch.createStarted();
			for (Edge e : graph.getEdges()) {
				String uri = Graphs.shortUriOfEdge(e);
				logger.trace("Found uri {}", uri); // prevent compiler optimization from removing previous call
			}
			logger.info("Traversed over all edges uris in {}", edgesUriTime);
		}
	};

	abstract void warmup(Graph graph);

	private static final Logger logger = LoggerFactory.getLogger(GraphWarmup.class);
	private static final String CONFIG_WARMUP = "graph.warmup";

	/**
	 * Perform graph warm-up steps as configured in the provided configuration.
	 */
	public static void byConfig(Graph graph, Configuration config) {
		logger.info("Starting graph warmup");
		List<GraphWarmup> warmups = EnumUtils.enumsfromConfig(GraphWarmup.class, config, CONFIG_WARMUP);
		for (GraphWarmup warmup : warmups)
			warmup.warmup(graph);
		logger.info("Done with graph warmup");
	}
}
