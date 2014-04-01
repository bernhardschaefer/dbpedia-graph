package de.unima.dws.dbpediagraph.weights;

import java.util.Map;

import com.tinkerpop.blueprints.Edge;

import de.unima.dws.dbpediagraph.weights.EdgeWeightsFactory.EdgeWeightsType;

/**
 * Combined Information Content edge weights implementation as described in Schuhmacher & Ponzetto (2014).
 * 
 * @author Bernhard Schäfer
 * 
 */
public class CombinedInformationContent extends AbstractEdgeWeightOccsCountAdapter implements EdgeWeights {

	public CombinedInformationContent(Map<String, Integer> occCounts) {
		super(occCounts);
	}

	@Override
	public Double transform(Edge e) {
		// w_combIC(e) = IC(w_Pred ) + IC(w_Obj) .
		String pred = getPred(e);
		String obj = getObj(e);
		double score = ic(pred) + ic(obj);
		logEdgeScore(e, score);
		return score;
	}

	@Override
	public EdgeWeightsType type() {
		return EdgeWeightsType.COMB_IC;
	}

}
