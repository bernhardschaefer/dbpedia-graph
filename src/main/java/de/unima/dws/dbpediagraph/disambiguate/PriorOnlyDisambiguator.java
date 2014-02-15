package de.unima.dws.dbpediagraph.disambiguate;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Graph;

import de.unima.dws.dbpediagraph.graph.GraphType;
import de.unima.dws.dbpediagraph.model.*;
import de.unima.dws.dbpediagraph.weights.EdgeWeights;

public class PriorOnlyDisambiguator<T extends SurfaceForm, U extends Sense> implements LocalGraphDisambiguator<T, U> {
	private static final Logger logger = LoggerFactory.getLogger(PriorOnlyDisambiguator.class);

	public PriorOnlyDisambiguator(GraphType graphType, EdgeWeights edgeWeights) {
	}

	@Override
	public Map<T, List<SurfaceFormSenseScore<T, U>>> allSurfaceFormSensesScores(Map<T, List<U>> surfaceFormsSenses,
			Graph subgraph) {
		return bestK(surfaceFormsSenses, subgraph, Integer.MAX_VALUE);
	}

	@Override
	public Map<T, List<SurfaceFormSenseScore<T, U>>> bestK(Map<T, List<U>> surfaceFormsSenses, Graph subgraph, int k) {
		logger.info("Using disambiguator {}", this);

		Map<T, List<SurfaceFormSenseScore<T, U>>> senseScores = new HashMap<>();

		// for each surface form store the k candidate senses with the highest score
		for (T surfaceForm : surfaceFormsSenses.keySet()) {
			List<U> sFSenses = surfaceFormsSenses.get(surfaceForm);

			List<SurfaceFormSenseScore<T, U>> sfss = new ArrayList<>();
			for (U sense : sFSenses) {
				sfss.add(new SurfaceFormSenseScore<T, U>(surfaceForm, sense, sense.prior()));
			}

			// take best k
			Collections.sort(sfss, SurfaceFormSenseScore.DESCENDING_SCORE_COMPARATOR);
			int toIndex = k > sfss.size() ? sfss.size() : k;
			senseScores.put(surfaceForm, sfss.subList(0, toIndex));
		}

		return senseScores;
	}

	@Override
	public List<SurfaceFormSenseScore<T, U>> disambiguate(Map<T, List<U>> surfaceFormsSenses, Graph subgraph) {
		Map<T, List<SurfaceFormSenseScore<T, U>>> allScores = allSurfaceFormSensesScores(surfaceFormsSenses, subgraph);

		List<SurfaceFormSenseScore<T, U>> highestScores = new ArrayList<>();
		for (T surfaceForm : allScores.keySet()) {
			List<SurfaceFormSenseScore<T, U>> sFSScores = allScores.get(surfaceForm);
			if (sFSScores.isEmpty()) {
				logger.warn("Surface form {} has no sense candidates.", surfaceForm);
				continue;
			}
			SurfaceFormSenseScore<T, U> highestScoreSense = Collections.max(sFSScores,
					SurfaceFormSenseScore.DESCENDING_SCORE_COMPARATOR);
			highestScores.add(highestScoreSense);
		}

		return highestScores;
	}

}
