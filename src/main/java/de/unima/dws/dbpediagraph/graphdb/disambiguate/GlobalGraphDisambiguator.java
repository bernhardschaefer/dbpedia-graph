package de.unima.dws.dbpediagraph.graphdb.disambiguate;

import java.util.*;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.model.*;

/**
 * {@link GraphDisambiguator} for global graph connectivity measures. Unlike local connectivity measures, in the global
 * setting a score can only be determined for an assignment of one candidate sense for each surface form.
 * 
 * @author Bernhard Schäfer
 * 
 */
public interface GlobalGraphDisambiguator<T extends SurfaceForm, U extends Sense> extends GraphDisambiguator<T, U> {

	@Override
	public List<SurfaceFormSenseScore<T, U>> disambiguate(Map<T, List<U>> surfaceFormsSenses, Graph subgraph);

	/**
	 * Determine the global connectivity score for a fixed set of candidate sense assignments represented as vertices.
	 * 
	 * @param assigments
	 *            the surface form sense assignments as vertices
	 * @param subgraph
	 *            the subgraph consists of all paths between all senses; not just the provided senses.
	 * @param senseVertices TODO javadoc
	 * @return the score for the provided assignments
	 */
	public double globalConnectivityMeasure(Collection<Vertex> assigments, Graph subgraph, Set<Vertex> sensesVertices);

	/**
	 * Convenience method for {@link #globalConnectivityMeasure(Collection, Graph, Set)}.
	 */
	public double globalConnectivityMeasure(Map<T, U> surfaceFormSenseAssigments, Graph subgraph, Set<Vertex> sensesVertices);
}
