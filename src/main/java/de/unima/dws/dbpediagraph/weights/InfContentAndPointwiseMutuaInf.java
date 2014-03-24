package de.unima.dws.dbpediagraph.weights;

import java.util.Map;

import com.tinkerpop.blueprints.Edge;

import de.unima.dws.dbpediagraph.graph.Graphs;
import de.unima.dws.dbpediagraph.weights.EdgeWeightsFactory.EdgeWeightsType;

public class InfContentAndPointwiseMutuaInf extends AbstractEdgeWeightOccsCountAdapter {

	public InfContentAndPointwiseMutuaInf(Map<String, Integer> occCounts) {
		super(occCounts);
	}

	@Override
	public Double transform(Edge e) {
		// PMI(w_Pred , w_Obj ) = log( P(w_Pred,w_Obj) / ( P(w_Pred) * P(w_Obj) ) )
		String pred = getPred(e);
		String obj = getObj(e);
		double score = Math.log(p(pred + obj) / (p(pred) * p(obj))); 
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("{}: {}", Graphs.edgeToString(e), score);
		return score;
	}

	@Override
	public EdgeWeightsType type() {
		return EdgeWeightsType.IC_PMI;
	}
}
