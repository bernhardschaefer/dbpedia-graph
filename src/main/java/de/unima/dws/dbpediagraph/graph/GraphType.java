package de.unima.dws.dbpediagraph.graph;

import com.tinkerpop.blueprints.Direction;

import de.unima.dws.dbpediagraph.disambiguate.GraphDisambiguator;

/**
 * Enum to distinguish between directed and undirected graphs.
 * 
 * @author Bernhard Schäfer
 * 
 */
public enum GraphType {
	DIRECTED_GRAPH {
		@Override
		public Direction getDirection() {
			return Direction.IN;
		}
	},
	UNDIRECTED_GRAPH {
		@Override
		public Direction getDirection() {
			return Direction.BOTH;
		}
	};

	/**
	 * The direction of a graph type is relevant for {@link GraphDisambiguator} implementations.
	 * 
	 * @return the direction corresponding to this graph type.
	 */
	public abstract Direction getDirection();

}
