package de.unima.dws.dbpediagraph.graphdb.subgraph;

import de.unima.dws.dbpediagraph.graphdb.GraphType;
import de.unima.dws.dbpediagraph.graphdb.filter.DummyEdgeFilter;
import de.unima.dws.dbpediagraph.graphdb.filter.EdgeFilter;

public final class SubgraphConstructionSettings {

	// parameters are initialized to default values
	ExplorationThreshold explorationThreshold = DegreeThreshold.getDefault();
	EdgeFilter edgeFilter = new DummyEdgeFilter();
	GraphType graphType = GraphType.DIRECTED_GRAPH;
	/**
	 * "the distance between two vertices in a graph is the <i>number of edges</i> in a shortest path connecting them."
	 * 
	 * @see <a
	 *      href="http://en.wikipedia.org/wiki/Distance_(graph_theory)">http://en.wikipedia.org/wiki/Distance_(graph_theory)</a>
	 */
	int maxDistance = 4;

	private static final SubgraphConstructionSettings DEFAULT = new SubgraphConstructionSettings();

	public static SubgraphConstructionSettings getDefault() {
		return DEFAULT;
	}

	public SubgraphConstructionSettings edgeFilter(EdgeFilter edgeFilter) {
		this.edgeFilter = edgeFilter;
		return this;
	}

	public SubgraphConstructionSettings graphType(GraphType graphType) {
		this.graphType = graphType;
		return this;
	}

	public SubgraphConstructionSettings maxDistance(int maxDistance) {
		this.maxDistance = maxDistance;
		return this;
	}

}
