package de.unima.dws.dbpediagraph.graphdb.disambiguate;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.model.Sense;
import de.unima.dws.dbpediagraph.graphdb.model.SurfaceForm;
import de.unima.dws.dbpediagraph.graphdb.model.SurfaceFormSenseScore;

/**
 * {@link GraphDisambiguator} for global graph connectivity measures. Unlike
 * local connectivity measures, in the global setting a score can only be
 * determined for an assignment of one candidate sense for each surface form.
 * 
 * @author Bernhard Schäfer
 * 
 */
public interface GlobalGraphDisambiguator<T extends SurfaceForm, U extends Sense> extends GraphDisambiguator<T, U> {
	@Override
	public List<SurfaceFormSenseScore<T, U>> disambiguate(Map<T, List<U>> surfaceFormsSenses, Graph subgraph);

	// TODO javadoc
	public double globalConnectivityMeasure(Collection<Vertex> surfaceFormSenseAssigments, Graph subgraph);

	// TODO javadoc
	public double globalConnectivityMeasure(Graph senseAssignmentsGraph);

	/**
	 * Retrieve the global connectivity measure score for a sense assignment
	 * 
	 * @param surfaceFormSenseAssigments
	 *            the sense assignments; for each surface form exactly one
	 *            candidate sense is selected
	 * @param subgraph
	 *            the subgraph consists of all paths between the sense
	 *            assignments
	 * @return the score for the provided assignments
	 */
	public double globalConnectivityMeasure(Map<T, U> surfaceFormSenseAssigments, Graph subgraph);
}
