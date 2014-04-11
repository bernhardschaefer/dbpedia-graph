package de.unima.dws.dbpediagraph.weights;

import java.util.Map;

import com.tinkerpop.blueprints.Edge;

import de.unima.dws.dbpediagraph.weights.EdgeWeightsFactory.EdgeWeightsType;

/**
 * Joint Information Content edge weights implementation as described in Schuhmacher & Ponzetto (2014).
 * 
 * @author Bernhard Schäfer
 * 
 */
public class JointInformationContent extends AbstractEdgeWeightOccsCountAdapter {

	public JointInformationContent(Map<String, Integer> occCounts) {
		super(occCounts);
	}

	@Override
	public Double transform(Edge e) {
		// w_jointIC(e) = IC(w_Pred) + IC(w_Obj | w_Pred) = IC(w_Pred) + (-1) * ln(P(w_Pred,w_Obj)/P(w_Pred))
		String pred = getPred(e);
		String obj = getObj(e);
		double icObjGivenPred = -1 * Math.log(p(pred + obj) / p(pred));
		double score = ic(pred) + icObjGivenPred;
		logEdgeScore(e, score);
		return score;
	}

	@Override
	public EdgeWeightsType type() {
		return EdgeWeightsType.JOINT_IC;
	}

}
