package de.unima.dws.dbpediagraph.search;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Graph;

import de.unima.dws.dbpediagraph.model.Sense;
import de.unima.dws.dbpediagraph.model.SurfaceForm;

/**
 * Switching {@link Searcher} implementation that switches between implementations depending on the request.
 * 
 * @author Bernhard Schäfer
 * 
 */
class SwitchingSearcher implements Searcher {
	private static final Logger logger = LoggerFactory.getLogger(SwitchingSearcher.class);

	private final Searcher bruteForce;
	private final Searcher simulatedAnnealing;
	private final int maxIterations;

	SwitchingSearcher(int maxIterations) {
		this.maxIterations = maxIterations;
		bruteForce = new BruteForceSearcher();
		simulatedAnnealing = new SimulatedAnnealingSearcher(new AimaScheduler(maxIterations));
	}

	@Override
	public <T extends SurfaceForm, U extends Sense> Map<T, U> search(Map<T, List<U>> surfaceFormsSenses,
			Graph subgraph, ConnectivityMeasureFunction<T, U> measureFunction) {
		int assignmentCombinations = getCombinations(surfaceFormsSenses.values());
		// use brute force is there are less than maxIterations combinations; otherwise use search heuristic
		Searcher searcher = assignmentCombinations <= maxIterations ? bruteForce : simulatedAnnealing;
		logger.info("Found {} assignment combinations (threshold {}). Using {}", assignmentCombinations, maxIterations,
				searcher.getClass().getSimpleName());
		return searcher.search(surfaceFormsSenses, subgraph, measureFunction);
	}

	private static int getCombinations(Collection<? extends List<?>> surfaceFormsSenses) {
		int combinations = 1;
		for (List<?> senses : surfaceFormsSenses)
			combinations *= senses.size();
		return combinations;
	}

}
