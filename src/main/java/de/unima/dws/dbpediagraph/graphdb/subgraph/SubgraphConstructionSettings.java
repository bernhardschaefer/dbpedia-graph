package de.unima.dws.dbpediagraph.graphdb.subgraph;

import org.apache.commons.configuration.Configuration;

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
	// keys in configuration file
	private static final String MAX_DISTANCE_KEY = "de.unima.dws.dbpediagraph.graphdb.subgraph.maxDistance";
	private static final String GRAPH_TYPE_KEY = "de.unima.dws.dbpediagraph.graphdb.subgraph.graphType";
	private static final String EXPLORATION_THRESHOLD_KEY = "de.unima.dws.dbpediagraph.graphdb.subgraph.explorationThreshold";
	private static final String EDGE_FILTER_KEY = "de.unima.dws.dbpediagraph.graphdb.subgraph.edgeFilter";

	/**
	 * Instance with default settings
	 */
	private static final SubgraphConstructionSettings DEFAULT = new SubgraphConstructionSettings.Builder().build();

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

	/**
	 * Get the instance with default settings
	 */
	public static SubgraphConstructionSettings getDefault() {
		return DEFAULT;
	}

	public static SubgraphConstructionSettings fromConfig(Configuration config) {
		SubgraphConstructionSettings.Builder builder = new SubgraphConstructionSettings.Builder();

		Integer maxDistance = config.getInt(MAX_DISTANCE_KEY);
		if (maxDistance != null)
			builder.maxDistance(maxDistance);

		String graphTypeString = config.getString(GRAPH_TYPE_KEY);
		if (graphTypeString != null) {
			GraphType graphType = GraphType.valueOf(graphTypeString);
			builder.graphType(graphType);
		}

		String explorationThresholdClassName = config.getString(EXPLORATION_THRESHOLD_KEY);
		if (explorationThresholdClassName != null) {
			// TODO implement
		}

		String edgeFilterClassName = config.getString(EDGE_FILTER_KEY);
		if (edgeFilterClassName != null) {
			// TODO implement
		}

		return builder.build();
	}

	public static class Builder {
		// parameters are initialized to default values
		private ExplorationThreshold explorationThreshold = DegreeThreshold.getDefault();
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

		public Builder explorationThreshold(ExplorationThreshold explorationThreshold) {
			this.explorationThreshold = explorationThreshold;
			return this;
		}
	}

}
