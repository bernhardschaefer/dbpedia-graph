package de.unima.dws.dbpediagraph.graphdb.filter;

import java.util.Iterator;

import com.tinkerpop.blueprints.Edge;

/**
 * Abstract edge filter implementation. Implementing classes only need to
 * implement the method {@link #isValidEdge(Edge)}.
 * 
 * @author Bernhard Schäfer
 * 
 */
public abstract class AbstractEdgeFilter implements EdgeFilter {

	/**
	 * Iterator implementation. For each request it continues iterating on the
	 * provided iterator until {@link #isValidEdge(Edge)} is satisfied.
	 * 
	 * @author Bernhard Schäfer
	 * 
	 */
	protected class ValidEdgeIterator implements Iterator<Edge> {
		private final Iterator<Edge> iterator;
		private Edge next;

		public ValidEdgeIterator(Iterator<Edge> iterator) {
			this.iterator = iterator;
			next = fetchNext();
		}

		private Edge fetchNext() {
			while (iterator.hasNext()) {
				Edge e = iterator.next();
				if (isValidEdge(e)) {
					return e;
				}
			}
			return null;
		}

		@Override
		public boolean hasNext() {
			return next != null;
		}

		@Override
		public Edge next() {
			// TODO check if this works or pointer conflict
			Edge toReturn = next;
			next = fetchNext();
			return toReturn;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	protected Iterator<Edge> iterator;

	public AbstractEdgeFilter() {
	}

	public AbstractEdgeFilter(Iterable<Edge> iterable) {
		this(iterable.iterator());
	}

	public AbstractEdgeFilter(Iterator<Edge> iterator) {
		this.iterator = iterator;
	}

	@Override
	public abstract boolean isValidEdge(Edge e);

	@Override
	public Iterator<Edge> iterator() {
		return new ValidEdgeIterator(iterator);
	}

	public void setIterator(Iterator<Edge> iterator) {
		this.iterator = iterator;
	}
}
