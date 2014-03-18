package de.unima.dws.dbpediagraph.weights;

import java.util.Map;

import com.tinkerpop.blueprints.Edge;

import de.unima.dws.dbpediagraph.weights.EdgeWeightsFactory.EdgeWeightsType;

public class CombinedInformationContent extends AbstractEdgeWeightOccsCountAdapter {

	public CombinedInformationContent(Map<String, Integer> occCounts) {
		super(occCounts);
	}

	@Override
	public Double transform(Edge e) {
		// w_combIC(e) = IC(w_Pred ) + IC(w_Obj) .
		String pred = getPred(e);
		String obj = getObj(e);
		return ic(pred) + ic(obj);
	}

	@Override
	public EdgeWeightsType type() {
		return EdgeWeightsType.COMB_IC;
	}

}
