package de.unima.dws.dbpediagraph.graphdb.search;

import java.util.*;

import com.tinkerpop.blueprints.Graph;

import de.unima.dws.dbpediagraph.graphdb.model.Sense;
import de.unima.dws.dbpediagraph.graphdb.model.SurfaceForm;

class StrategySearcher implements Searcher {
	private final Searcher bruteForce;
	private final Searcher simmulatedAnnealing;
	private final int maxIterations;

	StrategySearcher(int maxIterations) {
		this.maxIterations = maxIterations;
		bruteForce = new BruteForceSearcher();
		simmulatedAnnealing = new SimulatedAnnealingSearcher(new AimaScheduler(maxIterations));
	}

	@Override
	public <T extends SurfaceForm, U extends Sense> Map<T, U> search(Map<T, List<U>> surfaceFormsSenses,
			Graph subgraph, ConnectivityMeasureFunction<T, U> measureFunction) {
		int combinations = getCombinations(surfaceFormsSenses.values());
		if (combinations <= maxIterations)
			return bruteForce.search(surfaceFormsSenses, subgraph, measureFunction);
		else
			return simmulatedAnnealing.search(surfaceFormsSenses, subgraph, measureFunction);
	}

	private static int getCombinations(Collection<? extends List<?>> surfaceFormsSenses) {
		int combinations = 1;
		for (List<?> senses : surfaceFormsSenses)
			combinations *= senses.size();
		// for (int i = 0; i < surfaceFormsSenses.size(); i++) {
		// combinations *= (surfaceFormsSenses.get(i).size() - i);
		// }
		return combinations;
	}

}
