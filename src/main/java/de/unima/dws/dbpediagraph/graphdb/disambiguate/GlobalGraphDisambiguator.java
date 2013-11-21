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
	 * @param surfaceFormsVertices TODO javadoc
	 * @return the score for the provided assignments
	 */
	public double globalConnectivityMeasure(Collection<Vertex> assigments, Graph subgraph, Collection<Set<Vertex>> surfaceFormsVertices);

	/**
	 * Determine the global connectivity score for a fixed set of candidate sense assignments provided as a graph.
	 * 
	 * @param subsubgraph
	 *            the sense graph consists of all paths between the sense assignments
	 * @return the score for the provided assignments
	 */
	public double globalConnectivityMeasure(Graph subsubgraph);

	/**
	 * Determine the global connectivity score for a fixed set of candidate sense assignments where each surface form is
	 * mapped to exactly one candidate sense.
	 * 
	 * @param surfaceFormSenseAssigments
	 *            the sense assignments; for each surface form exactly one candidate sense is selected
	 * @param subgraph
	 *            the subgraph consists of all paths between all senses; not just the provided senses.
	 * @param surfaceFormsSenses TODO javadoc
	 * @return the score for the provided assignments
	 */
	public double globalConnectivityMeasure(Map<T, U> surfaceFormSenseAssigments, Graph subgraph, Map<T, List<U>> surfaceFormsSenses);
}
