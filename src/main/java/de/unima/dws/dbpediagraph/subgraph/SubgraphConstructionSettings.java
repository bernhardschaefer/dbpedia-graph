package de.unima.dws.dbpediagraph.subgraph;

import org.apache.commons.configuration.Configuration;

import de.unima.dws.dbpediagraph.filter.DummyEdgeFilter;
import de.unima.dws.dbpediagraph.filter.EdgeFilter;
import de.unima.dws.dbpediagraph.graph.GraphType;

/**
 * Immutable holder class for parameters relevant for constructing a subgraph using a {@link SubgraphConstruction}.
 * Instances are created using the internal {@link Builder} class.
 * 
 * @author Bernhard Schäfer
 * 
 */
public final class SubgraphConstructionSettings {
	// keys in configuration file
	private static final String CONFIG_MAX_DISTANCE = "de.unima.dws.dbpediagraph.graphdb.subgraph.maxDistance";
	private static final String CONFIG_GRAPH_TYPE = "de.unima.dws.dbpediagraph.graphdb.subgraph.graphType";
	private static final String CONFIG_EXPLORATION_THRESHOLD = "de.unima.dws.dbpediagraph.graphdb.subgraph.explorationThreshold";
	private static final String CONFIG_EDGE_FILTER = "de.unima.dws.dbpediagraph.graphdb.subgraph.edgeFilter";

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

		Integer maxDistance = config.getInt(CONFIG_MAX_DISTANCE);
		if (maxDistance != null)
			builder.maxDistance(maxDistance);

		String graphTypeString = config.getString(CONFIG_GRAPH_TYPE);
		if (graphTypeString != null) {
			GraphType graphType = GraphType.valueOf(graphTypeString);
			builder.graphType(graphType);
		}

		String explorationThresholdClassName = config.getString(CONFIG_EXPLORATION_THRESHOLD);
		if (explorationThresholdClassName != null) {
			// TODO implement
		}

		String edgeFilterClassName = config.getString(CONFIG_EDGE_FILTER);
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
