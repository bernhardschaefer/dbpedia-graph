package de.unima.dws.dbpediagraph.graph;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.*;
import com.tinkerpop.blueprints.impls.neo4j.Neo4jGraph;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.util.wrappers.batch.BatchGraph;

/**
 * Noninstantiable graph factory class that provides graph instances.
 * 
 * @author Bernhard Schäfer
 * 
 */
public final class GraphFactory {
	private static final Logger logger = LoggerFactory.getLogger(GraphFactory.class);

	/**
	 * DBpedia Graph Holder is loaded on the first execution of {@link GraphFactory#getDBpediaGraph()} or the first
	 * access to DBpediaGraphHolder.GRAPH, not before. This lazy initialization is beneficial in case the graph is not
	 * accessed.
	 */
	private static class DBpediaGraphHolder {
		public static final TransactionalGraph GRAPH = openGraph(true);
	}

	/**
	 * Returns the batch graph instance that can be used for bulk inserting nodes and vertices into the dbpedia graph.
	 * If a persisted dbpedia graph exists already it is returned, otherwise a new graph is created.
	 * 
	 * @param bufferSize
	 *            the buffer size used for the batch inserts.
	 * @return
	 */
	public static BatchGraph<? extends TransactionalGraph> getBatchGraph(long bufferSize) {
		TransactionalGraph graph = openGraph(false);
		BatchGraph<TransactionalGraph> bgraph = new BatchGraph<>(graph, bufferSize);

		// check if graph exists already
		long verticesCount = Graphs.verticesCount(graph);
		if (verticesCount != 0) {
			bgraph.setVertexIdKey(GraphConfig.URI_PROPERTY);
			bgraph.setLoadingFromScratch(false);
			logger.info("There is an existing graph with {} vertices.", verticesCount);
		}

		return bgraph;
	}

	/**
	 * Accessor for the dbpedia graph from outside the factory. This method is intended for functionality that wants to
	 * access the established DBpedia Graph.
	 * 
	 * @return the dbpedia graph
	 * @throws IllegalStateException
	 *             if there is no existing graph or the graph is empty.
	 */
	public static TransactionalGraph getDBpediaGraph() {
		return DBpediaGraphHolder.GRAPH;
	}

	/**
	 * Return a new non-persistent graph instance.
	 */
	public static Graph newInMemoryGraph() {
		return new TinkerGraph();
	}

	/**
	 * Open a graph based on configuration settings.
	 * 
	 * @throws IllegalStateException
	 *             if needsToExist==true and there is no existing graph with vertices.
	 */
	private static TransactionalGraph openGraph(boolean needsToExist) {
		long startTime = System.currentTimeMillis();

		Graph graph = com.tinkerpop.blueprints.GraphFactory.open(GraphConfig.config());
		if (needsToExist && Graphs.isEmptyGraph(graph))
			// TODO cleanup directory and delete empty graph that blueprints.GraphFactory created
			throw new IllegalStateException(String.format(
					"There is no existing graph with vertices in the directory %s. "
							+ "For graph-based disambiguation run the graph loader tool "
							+ "first to create a graph from data dumps.", GraphConfig.graphDirectory()));

		if (graph instanceof Neo4jGraph) {
			Neo4jGraph nGraph = (Neo4jGraph) graph;
			nGraph.createKeyIndex(GraphConfig.URI_PROPERTY, Vertex.class);
		}

		logger.info("Graph loading time {} sec", (System.currentTimeMillis() - startTime) / 1000.0);
		if (graph instanceof TransactionalGraph)
			return (TransactionalGraph) graph;
		else
			throw new IllegalArgumentException("Graph specified in properties needs to be a transactional graph.");
	}

	// Suppress default constructor for noninstantiability
	private GraphFactory() {
		throw new AssertionError();
	}
}