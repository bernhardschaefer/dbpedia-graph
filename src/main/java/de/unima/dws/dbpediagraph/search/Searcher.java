package de.unima.dws.dbpediagraph.search;

import java.util.List;
import java.util.Map;

import com.tinkerpop.blueprints.Graph;

import de.unima.dws.dbpediagraph.model.Sense;
import de.unima.dws.dbpediagraph.model.SurfaceForm;

/**
 * Searcher interface as a heuristic for global disambiguators.
 * 
 * @author Bernhard Schäfer
 * 
 */
public interface Searcher {

	<T extends SurfaceForm, U extends Sense> Map<T, U> search(Map<T, List<U>> surfaceFormsSenses, Graph subgraph,
			ConnectivityMeasureFunction<T, U> measureFunction);

}
