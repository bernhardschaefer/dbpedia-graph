package de.unima.dws.dbpediagraph.graphdb.subgraph;

import com.tinkerpop.blueprints.Direction;

import de.unima.dws.dbpediagraph.graphdb.filter.DummyEdgeFilter;
import de.unima.dws.dbpediagraph.graphdb.filter.EdgeFilter;

public final class SubgraphConstructionSettings {

	// parameters are initialized to default values
	/**
	 * "the distance between two vertices in a graph is the <i>number of edges</i> in a shortest path connecting them."
	 * 
	 * @see <a
	 *      href="http://en.wikipedia.org/wiki/Distance_(graph_theory)">http://en.wikipedia.org/wiki/Distance_(graph_theory)</a>
	 */
	int maxDistance = DEFAULT_MAX_DISTANCE;

	EdgeFilter edgeFilter = DEFAULT_EDGE_FILTER;

	// TODO implement using direction in findPath()
	Direction direction = DEFAULT_DIRECTION;

	private static final int DEFAULT_MAX_DISTANCE = 5;
	private static final EdgeFilter DEFAULT_EDGE_FILTER = new DummyEdgeFilter();
	private static final Direction DEFAULT_DIRECTION = Direction.OUT;

	private static final SubgraphConstructionSettings DEFAULT = new SubgraphConstructionSettings();

	public static SubgraphConstructionSettings getDefault() {
		return DEFAULT;
	}

	public SubgraphConstructionSettings direction(Direction direction) {
		this.direction = direction;
		return this;
	}

	public SubgraphConstructionSettings edgeFilter(EdgeFilter edgeFilter) {
		this.edgeFilter = edgeFilter;
		return this;
	}

	public SubgraphConstructionSettings maxDistance(int maxDistance) {
		this.maxDistance = maxDistance;
		return this;
	}
}
