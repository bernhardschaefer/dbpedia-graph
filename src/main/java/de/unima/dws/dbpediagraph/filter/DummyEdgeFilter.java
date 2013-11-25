package de.unima.dws.dbpediagraph.filter;

import java.util.Iterator;

import com.tinkerpop.blueprints.Edge;

/**
 * Dummy edge filter that yields all edges as valid.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class DummyEdgeFilter extends AbstractEdgeFilter {
	public DummyEdgeFilter() {
	}

	public DummyEdgeFilter(Iterable<Edge> iterable) {
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
