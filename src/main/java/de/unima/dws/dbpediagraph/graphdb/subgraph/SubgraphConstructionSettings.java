package de.unima.dws.dbpediagraph.graphdb.subgraph;

import de.unima.dws.dbpediagraph.graphdb.GraphType;
import de.unima.dws.dbpediagraph.graphdb.filter.DummyEdgeFilter;
import de.unima.dws.dbpediagraph.graphdb.filter.EdgeFilter;

/**
 * Immutable holder class for parameters relevant for constructing a subgraph using a {@link SubgraphConstruction}.
 * Instances are created using the internal {@link Builder} class.
 * 
 * @author Bernhard Schäfer
 * 
 */
public final class SubgraphConstructionSettings {

	public static class Builder {
		// parameters are initialized to default values
		private final ExplorationThreshold explorationThreshold = DegreeThreshold.getDefault();
		private EdgeFilter edgeFilter = new DummyEdgeFilter();
		private GraphType graphType = GraphType.DIRECTED_GRAPH;
		private int maxDistance = 4;

		public SubgraphConstructionSettings build() {
			return new SubgraphConstructionSettings(this);
		}

		public Builder edgeFilter(EdgeFilter edgeFilter) {
			this.edgeFilter = edgeFilter;
			return this;
		}

		public Builder graphType(GraphType graphType) {
			this.graphType = graphType;
			return this;
		}

		public Builder maxDistance(int maxDistance) {
			this.maxDistance = maxDistance;
			return this;
		}
	}

	private static final SubgraphConstructionSettings DEFAULT = new SubgraphConstructionSettings.Builder().build();

	public static SubgraphConstructionSettings getDefault() {
		return DEFAULT;
	}

	final ExplorationThreshold explorationThreshold;
	final EdgeFilter edgeFilter;
	public final GraphType graphType;
	/**
	 * "the distance between two vertices in a graph is the <i>number of edges</i> in a shortest path connecting them."
	 * 
	 * @see <a
	 *      href="http://en.wikipedia.org/wiki/Distance_(graph_theory)">http://en.wikipedia.org/wiki/Distance_(graph_theory)</a>
	 */
	final int maxDistance;

	private SubgraphConstructionSettings(Builder builder) {
		this.explorationThreshold = builder.explorationThreshold;
		this.edgeFilter = builder.edgeFilter;
		this.graphType = builder.graphType;
		this.maxDistance = builder.maxDistance;
	}

}
