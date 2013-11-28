package de.unima.dws.dbpediagraph.weights;

import java.util.Map;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;

import de.unima.dws.dbpediagraph.graph.GraphConfig;
import de.unima.dws.dbpediagraph.graph.Graphs;

public class CombinedInformationContent extends AbstractEdgeWeightOccsCountAdapter {

	public CombinedInformationContent(Map<String, Integer> occCounts) {
		super(occCounts);
	}

	@Override
	public Double transform(Edge e) {
		// w_combIC(e) = IC(w_Pred ) + IC(w_Obj) .
		String pred = e.getProperty(GraphConfig.URI_PROPERTY);
		String obj = Graphs.shortUriOfVertex(e.getVertex(Direction.IN));
		return ic(pred) + ic(obj);
	}

}
