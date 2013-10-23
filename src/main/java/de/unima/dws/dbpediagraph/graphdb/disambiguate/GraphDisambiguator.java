package de.unima.dws.dbpediagraph.graphdb.disambiguate;

import java.util.Collection;
import java.util.List;

import com.tinkerpop.blueprints.Graph;

/**
 * 
 * Disambiguator interface for disambiguation methods.
 * 
 * @author Bernhard Schäfer
 * 
 */
public interface GraphDisambiguator {
	/**
	 * Disambiguate the provided senses of all provided words using a subgraph. Exemplary senses:
	 * {{drink1,drink2},{milk1,milk2,milk3}}
	 * 
	 * @param surfaceFormsSenses
	 *            contains all words to disambiguate and their respective candidate senses
	 * @param subgraph
	 * @return
	 */
	public List<SurfaceFormSenseScore> disambiguate(Collection<SurfaceFormSenses> surfaceFormsSenses, Graph subgraph);

}
