package de.unima.dws.dbpediagraph.graphdb.search;

public class SearcherFactory {

	public static Searcher newSearcher(int maxIterations) {
		// return new StrategySearcher(maxIterations);
		return new SimulatedAnnealingSearcher(new AimaScheduler(maxIterations));
	}

}
