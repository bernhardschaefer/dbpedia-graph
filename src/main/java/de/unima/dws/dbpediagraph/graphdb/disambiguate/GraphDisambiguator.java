package de.unima.dws.dbpediagraph.graphdb.disambiguate;

import java.util.Collection;
import java.util.List;
import java.util.Map;

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
	 * Return the scores for the best k sense candidates of each surface form.
	 * The {@link GraphDisambiguator#disambiguate(Collection, Graph)} method can
	 * then select for each surface form the candidate sense with the highest
	 * score.
	 * 
	 * @param surfaceFormsSenses
	 *            the words to disambiguate and their respective candidate
	 *            senses
	 * @param subgraph
	 *            the subgraph of all paths between all candidate senses of
	 *            different surface forms
	 * @param k
	 *            the best k candidate senses for each surface form to select
	 * @return the map which contains for each surface form (key) a list of
	 *         candidates sense and their respective score
	 */
	public Map<T, List<SurfaceFormSenseScore<T, U>>> bestK(
			Collection<? extends SurfaceFormSenses<T, U>> surfaceFormsSenses, Graph subgraph, int k);

	/**
	 * Disambiguate the provided senses of all provided words using a subgraph.
	 * Exemplary senses: {{drink1,drink2},{milk1,milk2,milk3}}
	 * 
	 * @param surfaceFormsSenses
	 *            the words to disambiguate and their respective candidate
	 *            senses
	 * @param subgraph
	 *            the subgraph of all paths between all candidate senses of
	 *            different surface forms
	 * @return the sense with the highest score for each word
	 */
	// TODO the return type does not make sense for global disambiguators;
	// either change to Map<SurfaceForm,Sense> or check in Spotlight if score
	// should be supported
	// TODO change Collection<SurfaceFormSenses> to
	// Map<SurfaceForm,List/Collection<Sense>>
	public List<SurfaceFormSenseScore<T, U>> disambiguate(
			Collection<? extends SurfaceFormSenses<T, U>> surfaceFormsSenses, Graph subgraph);

}
