package de.unima.dws.dbpediagraph.graph;

import org.apache.commons.configuration.Configuration;

import com.tinkerpop.blueprints.Direction;

import de.unima.dws.dbpediagraph.subgraph.SubgraphConstruction;

/**
 * Enum to distinguish between directed and undirected graphs.
 * 
 * @author Bernhard Schäfer
 * 
 */
public enum GraphType {
	DIRECTED_GRAPH {
		@Override
		public Direction getTraversalDirection() {
			return Direction.OUT;
		}
	},
	UNDIRECTED_GRAPH {
		@Override
		public Direction getTraversalDirection() {
			return Direction.BOTH;
		}
	};

	/**
	 * The traversal direction of a graph type is mostly relevant for {@link SubgraphConstruction} implementations.
	 * 
	 * @return the direction corresponding to this graph type.
	 */
	public abstract Direction getTraversalDirection();

	private static final String CONFIG_GRAPH_TYPE = "de.unima.dws.dbpediagraph.graph.graphType";

	/**
	 * Retrieves a {@link GraphType} instance from the configuration object. In case there is no such mapping in the
	 * configuration, it returns null.
	 */
	public static GraphType fromConfig(Configuration config) {
		String graphTypeString = config.getString(CONFIG_GRAPH_TYPE);
		if (graphTypeString == null)
			return null;
		return GraphType.valueOf(graphTypeString);
	}

}
