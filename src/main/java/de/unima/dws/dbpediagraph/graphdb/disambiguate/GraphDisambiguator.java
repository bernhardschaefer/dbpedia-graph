package de.unima.dws.dbpediagraph.graphdb.disambiguate;

import java.util.Collection;
import java.util.List;

import com.tinkerpop.blueprints.Graph;

import de.unima.dws.dbpediagraph.graphdb.model.Sense;
import de.unima.dws.dbpediagraph.graphdb.model.SurfaceForm;
import de.unima.dws.dbpediagraph.graphdb.model.SurfaceFormSenseScore;
import de.unima.dws.dbpediagraph.graphdb.model.SurfaceFormSenses;

/**
 * 
 * Disambiguator interface for disambiguation methods.
 * 
 * @author Bernhard Schäfer
 * 
 */
public interface GraphDisambiguator<T extends SurfaceForm, U extends Sense> {
	/**
	 * Disambiguate the provided senses of all provided words using a subgraph. Exemplary senses:
	 * {{drink1,drink2},{milk1,milk2,milk3}}
	 * 
	 * @param surfaceFormsSenses
	 *            contains all words to disambiguate and their respective candidate senses
	 * @param subgraph
	 * @return
	 */
	public List<SurfaceFormSenseScore<T, U>> disambiguate(
			Collection<? extends SurfaceFormSenses<T, U>> surfaceFormsSenses, Graph subgraph);

}
