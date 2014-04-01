package de.unima.dws.dbpediagraph.disambiguate;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.tinkerpop.blueprints.Graph;

import de.unima.dws.dbpediagraph.model.*;

/**
 * {@link GraphDisambiguator} for local graph connectivity measures. Unlike global connectivity measures, in the local
 * setting a score can be determined for each sense of a surface form independent of the selection of the candidate
 * sense of other surface forms.
 * 
 * @author Bernhard Schäfer
 * 
 */
public interface LocalGraphDisambiguator<T extends SurfaceForm, U extends Sense> extends GraphDisambiguator<T, U> {
	/**
	 * Determine the score for each sense candidate of each surface form. The
	 * {@link GraphDisambiguator#disambiguate(Collection, Graph)} method can then select for each surface form the
	 * candidate sense with the highest score.
	 * 
	 * @param surfaceFormsSenses
	 *            the words to disambiguate and their respective candidate senses
	 * @param subgraph
	 *            the subgraph of all paths between all candidate senses of different surface forms
	 * @return the map which contains for each surface form (key) a list of candidates sense and their respective score
	 */
	public Map<T, List<SurfaceFormSenseScore<T, U>>> allSurfaceFormSensesScores(Map<T, List<U>> surfaceFormsSenses,
			Graph subgraph);

	@Override
	public List<SurfaceFormSenseScore<T, U>> disambiguate(Map<T, List<U>> surfaceFormsSenses, Graph subgraph);
}
