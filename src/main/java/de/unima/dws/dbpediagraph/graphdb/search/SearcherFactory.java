package de.unima.dws.dbpediagraph.graphdb.search;

/**
 * Factory for retrieving {@link Searcher} implementations.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class SearcherFactory {

	/**
	 * Get a new instance of a searcher implementation that searches at most maxIterations assignments.
	 */
	public static Searcher newSearcher(int maxIterations) {
		return new StrategySearcher(maxIterations);
	}

}
