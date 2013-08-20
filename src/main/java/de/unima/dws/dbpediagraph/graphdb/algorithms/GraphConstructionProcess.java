package de.unima.dws.dbpediagraph.graphdb.algorithms;

import java.util.List;

import com.tinkerpop.blueprints.Vertex;

public class GraphConstructionProcess {

	/**
	 * 
	 * @param vertices
	 *            the list contains the concatenations of all senses (e.g.
	 *            drink1, ... , drink5; milk1, ..., milk4)for each content word
	 *            (e.g. drink, milk),
	 */
	public void createSubgraph(List<Vertex> vertices) {
		// problem is about finding a vertex-induced subgraph
		// (http://mathworld.wolfram.com/Vertex-InducedSubgraph.html)
		// (http://www.edmath.org/MATtours/discrete/concepts/csubgr.html)

		// V = vertices
		// E = {}

		// perform a DFS from the vertex vertices(0) until we reach another
		// vertex from vertices(1-n).

		// add all edges and vertices on the path from both vertices
		// V = V.append(List<Vertex> path)
		// E = E.append(List<Edge> path)

		// backtrack to vertex before new vertex was discovered (path(n-2))

	}
}
