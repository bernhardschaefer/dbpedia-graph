package de.unima.dws.dbpediagraph.graphdb.filter;

import com.tinkerpop.blueprints.Edge;

import de.unima.dws.dbpediagraph.graphdb.GraphConfig;

/**
 * Predicate edge filter that only yields DBpedia predicate edges.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class PredicateEdgeFilter extends AbstractEdgeFilter {
	public PredicateEdgeFilter() {
	}

	public PredicateEdgeFilter(Iterable<Edge> iterable) {
		super(iterable);
	}

	@Override
	public boolean isValidEdge(Edge e) {
		return e.getLabel().equals(GraphConfig.EDGE_LABEL);
	}

}
