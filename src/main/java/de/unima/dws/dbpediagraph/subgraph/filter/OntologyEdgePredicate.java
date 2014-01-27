package de.unima.dws.dbpediagraph.subgraph.filter;

import com.google.common.base.Predicate;
import com.tinkerpop.blueprints.*;

import de.unima.dws.dbpediagraph.graph.Graphs;

class OntologyEdgePredicate implements Predicate<Edge> {
	private static final String RDF_TYPE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
	
	@Override
	public boolean apply(Edge e) {
		return !Graphs.fullUriOfEdge(e).equals(RDF_TYPE);
	}

}
