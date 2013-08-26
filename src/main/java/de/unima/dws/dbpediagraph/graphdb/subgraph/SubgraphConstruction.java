package de.unima.dws.dbpediagraph.graphdb.subgraph;

import java.util.Set;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

/**
 * Subgraph Construction interface which defines the essential methods. The
 * subgraph is a vertex-induced subgraph. This means that based on a subset of
 * vertices of a Graph G, a subgraph is supposed to contain the subset vertices,
 * and all edges and vertices on the paths between any two vertices of the
 * subset. A proper definition of a vertex-induced subgraph can be found here:
 * 
 * <pre>
 * <a href="http://mathworld.wolfram.com/Vertex-InducedSubgraph.html">http://mathworld.wolfram.com/Vertex-InducedSubgraph.html</a>
 * </pre>
 * 
 * 
 * @author Bernhard Schäfer
 * 
 */
public interface SubgraphConstruction {

	/**
	 * Create a subgraph based on the provided senses.
	 * 
	 * @param vertices
	 *            the list contains the concatenations of all senses (e.g.
	 *            drink1, ... , drink5; milk1, ..., milk4)for each content word
	 *            (e.g. drink, milk),
	 */
	public Graph createSubgraph(Set<Vertex> senses);

}
