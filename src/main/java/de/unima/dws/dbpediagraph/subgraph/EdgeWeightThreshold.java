package de.unima.dws.dbpediagraph.subgraph;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

/**
 * @author Bernhard Schäfer
 */
public class EdgeWeightThreshold implements ExplorationThreshold {

	@Override
	public boolean isBelowThreshold(Vertex v, Edge e) {
		// TODO implement
		return false;
	}

}
