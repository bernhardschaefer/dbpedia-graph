package de.unima.dws.dbpediagraph.graphdb.subgraph;

import java.util.Collection;
import java.util.Set;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

/**
 * Subgraph Construction interface which defines the essential methods. This means that based on a subset of vertices of
 * a Graph G, a subgraph is supposed to contain the subset vertices, and all edges and vertices on all paths between any
 * two vertices of the subset.
 * 
 * @author Bernhard Schäfer
 * 
 */
public interface SubgraphConstruction {

	/**
	 * Create a subgraph based on the provided vertices.
	 * 
	 * @param surfaceFormVertices
	 *            a collection that contains for each surfaceForm a collection of vertex candidates (e.g. {drink1, ... ,
	 *            drink5}, {milk1, ..., milk4} for the surface forms (drink, milk)).
	 */
	// TODO think about changing (at least inner) collections to Sets
	public Graph createSubgraph(Collection<Collection<Vertex>> surfaceFormVertices);

//	/**
//	 * Create a subgraph based on the provided senses.
//	 * 
//	 * @param surfaceFormSenses
//	 *            a collection that contains for each surfaceForm a collection of sense candidates (e.g. {drink1, ... ,
//	 *            drink5}, {milk1, ..., milk4} for the surface forms (drink, milk)).
//	 */
//	public Graph createSubgraph(Map<? extends SurfaceForm, ? extends List<? extends Sense>> surfaceFormSenses);

	public Graph createSubSubgraph(Collection<Vertex> assignments, Set<Vertex> allSensesVertices);

}
