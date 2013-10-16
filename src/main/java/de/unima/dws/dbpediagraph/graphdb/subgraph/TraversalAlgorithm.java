package de.unima.dws.dbpediagraph.graphdb.subgraph;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Graph;

import de.unima.dws.dbpediagraph.graphdb.filter.DummyEdgeFilter;
import de.unima.dws.dbpediagraph.graphdb.filter.EdgeFilter;

/**
 * Abstract class to be used by all graph traversal algorithm for constructing subgraphs or more general use cases.
 * Provides important fields a traversal algorithm usually utilizes.
 * 
 * @author Bernhard Schäfer
 * 
 */
abstract class TraversalAlgorithm {
	/**
	 * The graph to traverse on.
	 */
	protected Graph graph;

	/**
	 * "the distance between two vertices in a graph is the <i>number of edges</i> in a shortest path connecting them."
	 * 
	 * @see <a
	 *      href="http://en.wikipedia.org/wiki/Distance_(graph_theory)">http://en.wikipedia.org/wiki/Distance_(graph_theory)</a>
	 */
	protected final int maxDistance;

	protected final EdgeFilter edgeFilter;

	// TODO implement using direction in findPath()
	protected final Direction direction;

	/**
	 * default value for {@link #maxDistance}
	 */
	protected static final int DEFAULT_MAX_DISTANCE = 5;

	/**
	 * default value for {@link #edgeFilter}
	 */
	protected static final EdgeFilter DEFAULT_EDGE_FILTER = new DummyEdgeFilter();

	/**
	 * default value for {@link #direction}
	 */
	protected static final Direction DEFAULT_DIRECTION = Direction.OUT;

	public TraversalAlgorithm() {
		this(null, DEFAULT_MAX_DISTANCE, DEFAULT_EDGE_FILTER, DEFAULT_DIRECTION);
	}

	/**
	 * Convenience constructor using default values for non-provided fields.
	 */
	public TraversalAlgorithm(Graph graph) {
		this(graph, DEFAULT_MAX_DISTANCE, DEFAULT_EDGE_FILTER, DEFAULT_DIRECTION);
	}

	/**
	 * Convenience constructor using default values for non-provided fields.
	 */
	public TraversalAlgorithm(Graph graph, int maxDistance) {
		this(graph, maxDistance, new DummyEdgeFilter(), Direction.BOTH);
	}

	public TraversalAlgorithm(Graph graph, int maxDistance, EdgeFilter edgeFilter, Direction direction) {
		this.graph = graph;
		this.maxDistance = maxDistance;
		this.edgeFilter = edgeFilter;
		this.direction = direction;
	}

	public Graph getGraph() {
		return graph;
	}

	public void setGraph(Graph graph) {
		this.graph = graph;
	}
}
