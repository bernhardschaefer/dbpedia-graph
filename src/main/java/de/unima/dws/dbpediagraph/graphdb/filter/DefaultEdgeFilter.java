package de.unima.dws.dbpediagraph.graphdb.filter;

import java.util.Iterator;

import com.tinkerpop.blueprints.Edge;

public class DefaultEdgeFilter extends AbstractEdgeFilter {

	public DefaultEdgeFilter(Iterable<Edge> iterable) {
		super(iterable);
	}

	@Override
	public boolean isValidEdge(Edge e) {
		return true;
	}

	@Override
	public Iterator<Edge> iterator() {
		return iterator;
	}

}
