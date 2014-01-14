package de.unima.dws.dbpediagraph.disambiguate;

import java.util.*;
import java.util.Map.Entry;

import com.tinkerpop.blueprints.Graph;

import de.unima.dws.dbpediagraph.model.*;

/**
 * Decorates a {@link GraphDisambiguator} with {@link PriorStrategy} capabilities.
 * 
 * @author Bernhard Schäfer
 * 
 */
class PriorStrategyDisambiguatorDecorator<T extends SurfaceForm, U extends Sense> implements GraphDisambiguator<T, U> {
	private final GraphDisambiguator<T, U> disambiguator;
	private final PriorStrategy priorStrategy;

	public PriorStrategyDisambiguatorDecorator(GraphDisambiguator<T, U> disambiguator, PriorStrategy priorStrategy) {
		this.disambiguator = disambiguator;
		this.priorStrategy = priorStrategy;
	}

	@Override
	public Map<T, List<SurfaceFormSenseScore<T, U>>> bestK(Map<T, List<U>> surfaceFormsSenses, Graph subgraph, int k) {
		// we cannot call bestK with the provided k here already, because we might loose candidates with a high prior.
		Map<T, List<SurfaceFormSenseScore<T, U>>> allCandidates = disambiguator.bestK(surfaceFormsSenses, subgraph,
				Integer.MAX_VALUE);

		Map<T, List<SurfaceFormSenseScore<T, U>>> bestK = new HashMap<>();

		for (Entry<T, List<SurfaceFormSenseScore<T, U>>> entry : allCandidates.entrySet()) {
			T surfaceForm = entry.getKey();
			List<SurfaceFormSenseScore<T, U>> sfss = entry.getValue();
			priorStrategy.reviseScores(surfaceForm, sfss);

			// after modifying scores we can get the bestK
			Collections.sort(sfss, SurfaceFormSenseScore.DESCENDING_SCORE_COMPARATOR);
			int toIndex = k > sfss.size() ? sfss.size() : k;
			bestK.put(surfaceForm, sfss.subList(0, toIndex));
		}

		return bestK;
	}

	@Override
	public List<SurfaceFormSenseScore<T, U>> disambiguate(Map<T, List<U>> surfaceFormsSenses, Graph subgraph) {
		Map<T, List<SurfaceFormSenseScore<T, U>>> best1 = bestK(surfaceFormsSenses, subgraph, 1);

		List<SurfaceFormSenseScore<T, U>> result = new ArrayList<>();
		for (List<SurfaceFormSenseScore<T, U>> candidates : best1.values()) {
			if (!candidates.isEmpty())
				result.add(candidates.get(0));
		}
		return result;
	}

}
