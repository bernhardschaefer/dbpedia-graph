package de.unima.dws.dbpediagraph.graphdb.filter;

import com.tinkerpop.blueprints.Edge;

import de.unima.dws.dbpediagraph.graphdb.GraphConfig;

/**
 * 
 * @author Bernhard Schäfer
 * 
 */
public class PredicateEdgeFilter extends AbstractEdgeFilter {

	public PredicateEdgeFilter(Iterable<Edge> iterable) {
		super(iterable);
	}

	@Override
	public boolean isValidEdge(Edge e) {
		return e.getLabel().equals(GraphConfig.EDGE_LABEL);
	}

}
