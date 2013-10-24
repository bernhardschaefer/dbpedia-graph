package de.unima.dws.dbpediagraph.graphdb.disambiguate;

import java.util.Collection;

import com.tinkerpop.blueprints.Graph;

public interface GlobalGraphDisambiguator extends GraphDisambiguator {

	/**
	 * Use a global connectivity measure to find the sense assignments with the highest scores.
	 * 
	 * @param allWordsSenses
	 *            the senses to disambiguate; the inner set contains all sense for a word. Example:
	 *            {{drink1,drink2},{milk1,milk2,milk3}}
	 * @param subgraph
	 *            the subgraph used for disambiguation
	 * @return
	 */
	// public List<WeightedSenseAssignments> disambiguateGlobal(List<List<String>> allWordsSenses, Graph subgraph);

	/**
	 * Retrieve the global connectivity measure score for a sense assignment
	 * 
	 * @param senseAssignments
	 *            the sense assignments
	 * @param sensegraph
	 *            the sense graph consists of all paths between the sense assignments
	 * @return the score for the provided assignments
	 */
	public Double globalConnectivityMeasure(Collection<String> senseAssignments, Graph sensegraph);
}