package de.unima.dws.dbpediagraph.weights;

import java.util.Map;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;

import de.unima.dws.dbpediagraph.graph.GraphConfig;
import de.unima.dws.dbpediagraph.graph.Graphs;

public class InfContentAndPointwiseMutuaInf extends AbstractEdgeWeightOccsCountAdapter {

	public InfContentAndPointwiseMutuaInf(Map<String, Integer> occCounts) {
		super(occCounts);
	}

	@Override
	public Double transform(Edge e) {
		// PMI(w_Pred , w_Obj ) = log( P(w_Pred,w_Obj) / ( P(w_Pred) * P(w_Obj) ) )
		String pred = e.getProperty(GraphConfig.URI_PROPERTY);
		String obj = Graphs.shortUriOfVertex(e.getVertex(Direction.IN));
		return Math.log(p(pred + obj) / (p(pred) * p(obj)));
	}

}
