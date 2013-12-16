package de.unima.dws.dbpediagraph.weights;

import org.apache.commons.collections15.Transformer;

import com.tinkerpop.blueprints.Edge;

import de.unima.dws.dbpediagraph.weights.EdgeWeightsFactory.EdgeWeightsType;

/**
 * Uses {@link Transformer} interface to ease JUNG algorithms usage.
 * @author Bernhard Schäfer
 */
public interface EdgeWeights extends Transformer<Edge,Double> {

	@Override
	Double transform(Edge e);
	
	/**
	 * Retrieve the corresponding {@link EdgeWeightsType}.
	 */
	EdgeWeightsType type();
}
