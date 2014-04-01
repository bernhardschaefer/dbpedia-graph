package de.unima.dws.dbpediagraph.weights;

import com.tinkerpop.blueprints.Edge;

import de.unima.dws.dbpediagraph.weights.EdgeWeightsFactory.EdgeWeightsType;

/**
 * Hack which decorates an edge weights implementation by raising the weight of the decorated implementation by the
 * provided exponent.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class ExponentialEdgeWeightsDecorator implements EdgeWeights {
	private double exp;
	private EdgeWeights edgeWeights;

	public ExponentialEdgeWeightsDecorator(EdgeWeights edgeWeights, double exp) {
		this.edgeWeights = edgeWeights;
		this.exp = exp;
	}

	@Override
	public Double transform(Edge e) {
		return Math.pow(edgeWeights.transform(e), exp);
	}

	@Override
	public EdgeWeightsType type() {
		return EdgeWeightsType.EXP;
	}

}
