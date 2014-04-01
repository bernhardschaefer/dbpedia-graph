package de.unima.dws.dbpediagraph.subgraph;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

/**
 * Exploration threshold condition to prevent exploration of edges that do not satisfy a predicate.
 * 
 * @author Bernhard Schäfer
 */
public interface ExplorationThreshold {
	public boolean isBelowThreshold(Vertex v, Edge e);
}
