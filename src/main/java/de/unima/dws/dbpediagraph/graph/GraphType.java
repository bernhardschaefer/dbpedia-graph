package de.unima.dws.dbpediagraph.graph;

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

}
