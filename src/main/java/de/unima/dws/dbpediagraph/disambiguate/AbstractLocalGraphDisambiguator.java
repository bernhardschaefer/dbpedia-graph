package de.unima.dws.dbpediagraph.disambiguate;

import java.util.*;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graph.GraphType;
import de.unima.dws.dbpediagraph.graph.Graphs;
import de.unima.dws.dbpediagraph.model.*;
import de.unima.dws.dbpediagraph.weights.EdgeWeights;
import edu.uci.ics.jung.algorithms.scoring.VertexScorer;

/**
 * Skeleton class which eases the implementation of {@link GraphDisambiguator}. Subclasses only need to implement
 * {@link #getVertexScorer(Graph)}.
 * 
 * @author Bernhard Schäfer
 * 
 */
public abstract class AbstractLocalGraphDisambiguator<T extends SurfaceForm, U extends Sense> implements
		LocalGraphDisambiguator<T, U> {
	private static final Logger logger = LoggerFactory.getLogger(AbstractLocalGraphDisambiguator.class);

	protected final GraphType graphType;
	protected final EdgeWeights edgeWeights;

	private final boolean usePriorFallback;

	public AbstractLocalGraphDisambiguator(GraphType graphType, EdgeWeights edgeWeights, boolean usePriorFallback) {
		this.graphType = graphType;
		this.edgeWeights = edgeWeights;
		this.usePriorFallback = usePriorFallback;
	}

	@Override
	public Map<T, List<SurfaceFormSenseScore<T, U>>> allSurfaceFormSensesScores(Map<T, List<U>> surfaceFormsSenses,
			Graph subgraph) {
		return bestK(surfaceFormsSenses, subgraph, Integer.MAX_VALUE);
	}

	@Override
	public Map<T, List<SurfaceFormSenseScore<T, U>>> bestK(Map<T, List<U>> surfaceFormsSenses, Graph subgraph, int k) {
		logger.info("Using disambiguator {}", this);
		VertexScorer<Vertex, Double> vertexScorer = getVertexScorer(subgraph);

		Map<T, List<SurfaceFormSenseScore<T, U>>> senseScores = new HashMap<>();

		// for each surface form store the k candidate senses with the highest score
		for (T surfaceForm : surfaceFormsSenses.keySet()) {
			List<U> sFSenses = surfaceFormsSenses.get(surfaceForm);

			List<SurfaceFormSenseScore<T, U>> sfss = new ArrayList<>();
			for (U sense : sFSenses) { // get the score for each sense
				Vertex v = Graphs.vertexByFullUri(subgraph, sense.fullUri());
				double score = (v == null) ? -1 : vertexScorer.getVertexScore(v);
				sfss.add(new SurfaceFormSenseScore<T, U>(surfaceForm, sense, score));
			}

			Collections.sort(sfss, SurfaceFormSenseScore.DESCENDING_SCORE_COMPARATOR);
			int toIndex = k > sfss.size() ? sfss.size() : k;
			senseScores.put(surfaceForm, sfss.subList(0, toIndex));
		}

		if (usePriorFallback)
			handleSingletons(senseScores);

		return senseScores;
	}

	private void handleSingletons(Map<T, List<SurfaceFormSenseScore<T, U>>> senseScores) {
		logger.info("Using prior fallback for all candidate singletons.");
		for (Entry<T, List<SurfaceFormSenseScore<T, U>>> entry : senseScores.entrySet()) {
			T sf = entry.getKey();
			List<SurfaceFormSenseScore<T, U>> sfss = entry.getValue();
			if (Collections.max(sfss, SurfaceFormSenseScore.ASCENDING_SCORE_COMPARATOR).getScore() == 0) {
				logger.info("Surface form {} has only candidate singletons {}.", sf, sfss);
				// set priors as scores
				for (SurfaceFormSenseScore<?, ?> surfaceFormSenseScore : sfss) {
					Double prior = surfaceFormSenseScore.getSense().prior();
					if (prior != null)
						surfaceFormSenseScore.setScore(prior);
				}
			}
		}
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

	/**
	 * This method is called in {@link #disambiguate(Collection, Graph)} to retrieve a score for each vertex
	 * corresponding to a sense.
	 * 
	 * @param subgraph
	 *            the subgraph that the vertices are contained in
	 * @return a {@link VertexScorer} implementation
	 */
	protected abstract VertexScorer<Vertex, Double> getVertexScorer(Graph subgraph);

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[ graphType: " + graphType + " ]";
	}
}
