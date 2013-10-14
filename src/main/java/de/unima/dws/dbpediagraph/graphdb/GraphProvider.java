package de.unima.dws.dbpediagraph.graphdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.GraphFactory;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.neo4j.Neo4jGraph;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.util.wrappers.batch.BatchGraph;
import com.tinkerpop.gremlin.java.GremlinPipeline;

/**
 * Noninstantiable graph provider class that provides graph instances.
 * 
 * @author Bernhard Schäfer
 * 
 */
public final class GraphProvider {

	private static final Logger logger = LoggerFactory.getLogger(GraphProvider.class);

	private static final TransactionalGraph graph = openGraph();

	/**
	 * Returns the batch graph instance that can be used for bulk inserting nodes and vertices into the dbpedia graph.
	 * If a persisted dbpedia graph exists already it is returned, otherwise a new graph is created.
	 * 
	 * @param bufferSize
	 *            the buffer size used for the batch inserts.
	 * @return
	 */
	public static BatchGraph<? extends TransactionalGraph> getBatchGraph(long bufferSize) {
		BatchGraph<TransactionalGraph> bgraph = new BatchGraph<>(graph, bufferSize);

		// check if graph exists already
		long verticesCount = new GremlinPipeline<Object, Object>(graph.getVertices()).count();
		if (verticesCount != 0) {
			bgraph.setVertexIdKey(GraphConfig.URI_PROPERTY);
			bgraph.setLoadingFromScratch(false);
			logger.info("There is an existing graph with {} vertices.", verticesCount);
		}

		return bgraph;
	}

	/**
	 * Accessor for the dbpedia graph.
	 * 
	 * @return the dbpedia graph
	 */
	public static TransactionalGraph getDBpediaGraph() {
		return graph;
	}

	/**
	 * Return a new non-persistent graph instance.
	 */
	public static Graph newInMemoryGraph() {
		return new TinkerGraph();
	}

	/**
	 * Open a graph based on configuration settings.
	 */
	private static TransactionalGraph openGraph() {
		long startTime = System.currentTimeMillis();

		Graph graph = GraphFactory.open(GraphConfig.config());
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

	// Suppress default constructor for noninstantiability
	private GraphProvider() {
		throw new AssertionError();
	}
}
