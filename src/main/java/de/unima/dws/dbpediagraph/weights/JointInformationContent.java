package de.unima.dws.dbpediagraph.weights;

import java.util.Map;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;

import de.unima.dws.dbpediagraph.graph.GraphConfig;
import de.unima.dws.dbpediagraph.graph.Graphs;

public class JointInformationContent extends AbstractEdgeWeightOccsCountAdapter{

	public JointInformationContent(Map<String, Integer> occCounts) {
		super(occCounts);
	}

	@Override
	public Double weight(Edge e) {
		// w_jointIC(e) = IC(w_Pred) + IC(w_Obj | w_Pred)
		String pred = e.getProperty(GraphConfig.URI_PROPERTY);
		String obj = Graphs.shortUriOfVertex(e.getVertex(Direction.IN));
		return ic(pred) + ic(pred+obj) / ic(pred);
	}

}
