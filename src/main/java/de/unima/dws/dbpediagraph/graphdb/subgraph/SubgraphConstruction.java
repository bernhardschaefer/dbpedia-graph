package de.unima.dws.dbpediagraph.graphdb.subgraph;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.model.Sense;
import de.unima.dws.dbpediagraph.graphdb.model.SurfaceForm;

/**
 * Subgraph Construction interface which defines the essential methods. This
 * means that based on a subset of vertices of a Graph G, a subgraph is supposed
 * to contain the subset vertices, and all edges and vertices on all paths
 * between any two vertices of the subset.
 * 
 * @author Bernhard Schäfer
 * 
 */
public interface SubgraphConstruction {

	/**
	 * Create a subgraph based on the provided vertices.
	 * 
	 * @param senses
	 *            a collection of word senses contains the concatenations of all
	 *            senses (e.g. {drink1, ... , drink5}, {milk1, ..., milk4} ) for
	 *            each content word (e.g. drink, milk),
	 */
	public Graph createSubgraph(Collection<Collection<Vertex>> surfaceFormVertices);

	/**
	 * Create a subgraph based on the provided senses.
	 * 
	 * @param senses
	 *            a collection of word senses contains the concatenations of all
	 *            senses (e.g. {drink1, ... , drink5}, {milk1, ..., milk4} ) for
	 *            each content word (e.g. drink, milk),
	 */
	public Graph createSubgraph(Map<? extends SurfaceForm, ? extends List<? extends Sense>> surfaceFormSenses);

}
