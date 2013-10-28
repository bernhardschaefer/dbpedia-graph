package de.unima.dws.dbpediagraph.graphdb.disambiguate;

import java.util.Collection;
import java.util.List;

import com.tinkerpop.blueprints.Graph;

import de.unima.dws.dbpediagraph.graphdb.model.*;

/**
 * Skeleton class which eases the implementation of {@link GraphDisambiguator}.
 * Subclasses only need to implement
 * {@link #globalConnectivityMeasure(Collection, Graph)}.
 * 
 * @author Bernhard Schäfer
 * 
 */
public abstract class AbstractGlobalGraphDisambiguator<T extends SurfaceForm, U extends Sense> implements
		GraphDisambiguator<T, U> {

	/**
	 * Retrieve the global connectivity measure score for a sense assignment
	 * 
	 * @param senseAssignments
	 *            the sense assignments
	 * @param sensegraph
	 *            the sense graph consists of all paths between the sense
	 *            assignments
	 * @return the score for the provided assignments
	 */
	abstract public Double globalConnectivityMeasure(Collection<String> senseAssignments, Graph sensegraph);

	@Override
	public List<SurfaceFormSenseScore<T, U>> disambiguate(
			Collection<? extends SurfaceFormSenses<T, U>> surfaceFormsSenses, Graph subgraph) {
		// // Example allWordSenses = {{drink1,drink2},{milk1,milk2,milk3}}
		//
		// // iteration over all possible sense assignments , e.g.:
		// // 1st iteration: [drink1,milk1]
		// // 2nd iteration: [drink1,milk2]
		// // 3rd iteration: [drink1,milk3]
		// // 4th iteration: [drink2,milk1]
		// // ...
		//
		// // calculate global connectivity measure and add to result list
		//
		// // TODO implement genetic and simulated annealing functionality
		return null;
	}
}
