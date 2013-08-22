package de.unima.dws.dbpediagraph.graphdb.filter;

import java.util.Iterator;

import com.tinkerpop.blueprints.Edge;

/**
 * Edge filter functionalities to determine whether edges are valid. Graph
 * Algorithms can use this filter to limit the kind of edges they are working
 * on.
 * 
 * @author Bernhard Schäfer
 * 
 */
public interface EdgeFilter extends Iterable<Edge> {

	/**
	 * Predicate that decides if an edge is valid or not.
	 */
	boolean isValidEdge(Edge e);

	@Override
	Iterator<Edge> iterator();

}