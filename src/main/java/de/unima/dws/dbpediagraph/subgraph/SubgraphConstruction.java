package de.unima.dws.dbpediagraph.subgraph;

import java.util.*;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.model.Sense;
import de.unima.dws.dbpediagraph.model.SurfaceForm;

/**
 * Subgraph Construction interface. Based on a subset of vertices of a Graph G, a subgraph is supposed to contain the
 * subset vertices, and all edges and vertices on all paths between any two vertices of the subset.
 * 
 * @author Bernhard Schäfer
 * 
 */
public interface SubgraphConstruction {

	/**
	 * Create a subgraph based on the provided vertices.
	 * 
	 * @param surfaceFormVertices
	 *            a collection that contains for each surfaceForm a set of vertex candidates (e.g. {drink1, ... ,
	 *            drink5}, {milk1, ..., milk4} for the surface forms (drink, milk)).
	 */
	public Graph createSubgraph(Collection<Set<Vertex>> surfaceFormVertices);

	/**
	 * Convenience method which transforms the model-based representation to a vertex-centric representation and then
	 * delegates to {@link #createSubgraph(Collection)}.
	 */
	public Graph createSubgraph(Map<? extends SurfaceForm, ? extends List<? extends Sense>> surfaceFormSenses);

	public Graph createSubSubgraph(Collection<Vertex> assignments, Set<Vertex> allSensesVertices);

}
