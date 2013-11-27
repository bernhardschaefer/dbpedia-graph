package de.unima.dws.dbpediagraph.weights;

import com.tinkerpop.blueprints.Edge;

/**
 * @author Bernhard Schäfer
 */
public interface EdgeWeight {
	Double weight(Edge e);
}
