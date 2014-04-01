package de.unima.dws.dbpediagraph.weights;

import com.tinkerpop.blueprints.Edge;

import de.unima.dws.dbpediagraph.weights.EdgeWeightsFactory.EdgeWeightsType;

/**
 * Dummy edge weights implementation that is used when no edge weights are configured.
 * 
 * @author Bernhard Schäfer
 * 
 */
public enum DummyEdgeWeights implements EdgeWeights {
	INSTANCE;

	@Override
	public Double transform(Edge e) {
		return 1.0;
	}

	@Override
	public EdgeWeightsType type() {
		return EdgeWeightsType.DUMMY;
	}
}
