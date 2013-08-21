package de.unima.dws.dbpediagraph.graphdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.GraphFactory;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.neo4j.Neo4jGraph;
import com.tinkerpop.blueprints.util.wrappers.batch.BatchGraph;
import com.tinkerpop.gremlin.java.GremlinPipeline;

/**
 * Singleton that provides graph instances.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class GraphProvider {
	private static final Logger logger = LoggerFactory.getLogger(GraphProvider.class);

	private final TransactionalGraph graph;

	private GraphProvider() {
		graph = openGraph();
	}

	/**
	 * Creates and returns a graph implementation. The graph is created for
	 * batch inserts using the provided buffer size.
	 */
	public Graph getBatchGraph(long bufferSize) {
		BatchGraph<TransactionalGraph> bgraph = new BatchGraph<TransactionalGraph>(graph, bufferSize);

		// check if graph exists already
		long verticesCount = new GremlinPipeline<Object, Object>(graph.getVertices()).count();
		if (verticesCount != 0) {
			bgraph.setVertexIdKey(GraphConfig.URI_PROPERTY);
			bgraph.setLoadingFromScratch(false);
			logger.info("There is an existing graph with {} vertices.", verticesCount);
		}

		return bgraph;
	}

	public TransactionalGraph getGraph() {
		return graph;
	}

	/**
	 * Open a graph based on configuration settings.
	 */
	private TransactionalGraph openGraph() {
		long startTime = System.currentTimeMillis();

		Graph graph = GraphFactory.open(GraphConfig.getInstance().getConfig());
		if (graph instanceof Neo4jGraph) {
			Neo4jGraph nGraph = (Neo4jGraph) graph;
			nGraph.createKeyIndex(GraphConfig.URI_PROPERTY, Vertex.class);
		}

		logger.info("Graph loading time {} sec", (System.currentTimeMillis() - startTime) / 1000.0);
		if (graph instanceof TransactionalGraph) {
			return (TransactionalGraph) graph;
		} else {
			throw new IllegalArgumentException("Graph specified in properties needs to be a transactional graph.");
		}
	}

	/**
	 * Holder for the singleton.
	 */
	private static class Holder {
		public static final GraphProvider INSTANCE = new GraphProvider();
	}

	public static GraphProvider getInstance() {
		return Holder.INSTANCE;
	}
}
