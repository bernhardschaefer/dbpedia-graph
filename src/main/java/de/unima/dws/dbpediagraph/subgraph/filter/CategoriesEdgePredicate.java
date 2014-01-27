package de.unima.dws.dbpediagraph.subgraph.filter;

import com.google.common.base.Predicate;
import com.tinkerpop.blueprints.*;

import de.unima.dws.dbpediagraph.graph.Graphs;

class CategoriesEdgePredicate implements Predicate<Edge> {
	// e.g. http://dbpedia.org/resource/Category:Financial_institutions
	private static final String CATEGORY_PREFIX = "http://dbpedia.org/resource/Category:";

	@Override
	public boolean apply(Edge e) {
		return validVertex(e.getVertex(Direction.IN));
	}

	private static boolean validVertex(Vertex v) {
		return !Graphs.fullUriOfVertex(v).startsWith(CATEGORY_PREFIX);
	}
}
