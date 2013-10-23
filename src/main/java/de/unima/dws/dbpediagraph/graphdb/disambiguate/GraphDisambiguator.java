package de.unima.dws.dbpediagraph.graphdb.disambiguate;

import java.util.Collection;
import java.util.List;

import com.tinkerpop.blueprints.Graph;

/**
 * Disambiguator interface for disambiguation methods.
 * 
 * @author Bernhard Schäfer
 * 
 */
public interface GraphDisambiguator {
	/**
	 * 
	 * @param senses
	 *            the senses of all words to disambiguate; Example: {drink1,drink2,milk1,milk2,milk3}
	 * @param subgraph
	 * @return
	 */
	List<WeightedSense> disambiguate(Collection<String> senses, Graph subgraph);

}
