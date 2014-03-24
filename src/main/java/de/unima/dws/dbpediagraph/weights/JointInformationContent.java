package de.unima.dws.dbpediagraph.weights;

import java.util.Map;

import com.tinkerpop.blueprints.Edge;

import de.unima.dws.dbpediagraph.graph.Graphs;
import de.unima.dws.dbpediagraph.weights.EdgeWeightsFactory.EdgeWeightsType;

public class JointInformationContent extends AbstractEdgeWeightOccsCountAdapter {

	public JointInformationContent(Map<String, Integer> occCounts) {
		super(occCounts);
	}

	@Override
	public Double transform(Edge e) {
		// w_jointIC(e) = IC(w_Pred) + IC(w_Obj | w_Pred)
		String pred = getPred(e);
		String obj = getObj(e);
		double score = ic(pred) + ic(pred + obj) / ic(pred);
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("{}: {}", Graphs.edgeToString(e), score);
		return score;
	}

	@Override
	public EdgeWeightsType type() {
		return EdgeWeightsType.JOINT_IC;
	}

}
