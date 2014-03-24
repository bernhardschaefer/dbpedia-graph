package de.unima.dws.dbpediagraph.disambiguate;

import java.util.*;

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

	public AbstractLocalGraphDisambiguator(GraphType graphType, EdgeWeights edgeWeights) {
		this.graphType = graphType;
		this.edgeWeights = edgeWeights;
	}

	@Override
	public Map<T, List<SurfaceFormSenseScore<T, U>>> allSurfaceFormSensesScores(Map<T, List<U>> surfaceFormsSenses,
			Graph subgraph) {
		return bestK(surfaceFormsSenses, subgraph, Integer.MAX_VALUE);
	}

	@Override
	public Map<T, List<SurfaceFormSenseScore<T, U>>> bestK(Map<T, List<U>> surfaceFormsSenses, Graph subgraph, int k) {
		logger.info("Using disambiguator {}", this);

		Map<String, Vertex> fullUriToVertex = getVerticesByFullUris(surfaceFormsSenses, subgraph);

		VertexScorer<Vertex, Double> vertexScorer = getVertexScorer(subgraph,
				getVertexPriors(surfaceFormsSenses, fullUriToVertex));

		Map<T, List<SurfaceFormSenseScore<T, U>>> senseScores = new HashMap<>();

		// for each surface form store the k candidate senses with the highest score
		for (T surfaceForm : surfaceFormsSenses.keySet()) {
			List<U> sFSenses = surfaceFormsSenses.get(surfaceForm);

			List<SurfaceFormSenseScore<T, U>> sfss = new ArrayList<>();
			for (U sense : sFSenses) { // get the score for each sense
				Vertex v = fullUriToVertex.get(sense.fullUri());
				double score = (v == null) ? 0 : vertexScorer.getVertexScore(v);
				sfss.add(new SurfaceFormSenseScore<T, U>(surfaceForm, sense, score));
			}

			// take best k
			Collections.sort(sfss, SurfaceFormSenseScore.DESCENDING_SCORE_COMPARATOR);
			int toIndex = k > sfss.size() ? sfss.size() : k;
			senseScores.put(surfaceForm, sfss.subList(0, toIndex));
		}

		return senseScores;
	}

	private static <T extends SurfaceForm, U extends Sense> Map<String, Vertex> getVerticesByFullUris(
			Map<T, List<U>> surfaceFormsSenses, Graph subgraph) {
		Map<String, Vertex> fullUriToVertex = new HashMap<>();
		for (List<U> senses : surfaceFormsSenses.values()) {
			for (U sense : senses) {
				Vertex v = Graphs.vertexByFullUri(subgraph, sense.fullUri());
				if (v != null)
					fullUriToVertex.put(sense.fullUri(), v);
			}
		}
		return fullUriToVertex;
	}

	private static <T extends SurfaceForm, U extends Sense> Map<Vertex, Double> getVertexPriors(
			Map<T, List<U>> surfaceFormsSenses, Map<String, Vertex> fullUriToVertex) {
		Map<Vertex, Double> vertexPriors = new HashMap<>(fullUriToVertex.size());
		for (List<U> senses : surfaceFormsSenses.values()) {
			for (U sense : senses) {
				Vertex v = fullUriToVertex.get(sense.fullUri());
				if (v != null)
					vertexPriors.put(v, sense.prior());
			}
		}
		return vertexPriors;
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
					SurfaceFormSenseScore.SCORE_COMPARATOR);
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
	 * @param vertexPriors
	 *            the prior probabilities of the vertices
	 * @return a {@link VertexScorer} implementation
	 */
	protected abstract VertexScorer<Vertex, Double> getVertexScorer(Graph subgraph, Map<Vertex, Double> vertexPriors);

	@Override
	public String toString() {
		return String.format("%s [graphType: %s, edgeWeights: %s ]", getClass().getSimpleName(), graphType, edgeWeights
				.getClass().getSimpleName());
	}
}
