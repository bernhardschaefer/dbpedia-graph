package de.unima.dws.dbpediagraph.weights;

import com.tinkerpop.blueprints.Edge;

/**
 * @author Bernhard Schäfer
 */
public interface EdgeWeights {
	Double weight(Edge e);
}
