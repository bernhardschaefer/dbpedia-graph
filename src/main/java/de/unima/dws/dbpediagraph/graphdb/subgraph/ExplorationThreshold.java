package de.unima.dws.dbpediagraph.graphdb.subgraph;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

/**
 * @author Bernhard Schäfer
 */
public interface ExplorationThreshold {
	public boolean isBelowThreshold(Vertex v, Edge e);
}
