package de.unima.dws.dbpediagraph.disambiguate;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graph.GraphType;
import de.unima.dws.dbpediagraph.graph.Graphs;
import de.unima.dws.dbpediagraph.model.*;
import de.unima.dws.dbpediagraph.weights.EdgeWeight;
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
	protected final EdgeWeight graphWeights;

	public AbstractLocalGraphDisambiguator(GraphType graphType, EdgeWeight graphWeights) {
		this.graphType = graphType;
		this.graphWeights = graphWeights;
	}

	@Override
	public Map<T, List<SurfaceFormSenseScore<T, U>>> allSurfaceFormSensesScores(Map<T, List<U>> surfaceFormsSenses,
			Graph subgraph) {
		return bestK(surfaceFormsSenses, subgraph, Integer.MAX_VALUE);
	}

	private final Comparator<? super SurfaceFormSenseScore<T, U>> descendingScoreComparator = new Comparator<SurfaceFormSenseScore<T, U>>() {
		@Override
		public int compare(SurfaceFormSenseScore<T, U> left, SurfaceFormSenseScore<T, U> right) {
			return Double.compare(right.score(), left.score());
		}
	};

	@Override
	public Map<T, List<SurfaceFormSenseScore<T, U>>> bestK(Map<T, List<U>> surfaceFormsSenses, Graph subgraph, int k) {
		VertexScorer<Vertex, Double> vertexScorer = getVertexScorer(subgraph);

		Map<T, List<SurfaceFormSenseScore<T, U>>> senseScores = new HashMap<>();

		// for each surface form store the k candidate senses with the highest score
		for (T surfaceForm : surfaceFormsSenses.keySet()) {
			List<U> sFSenses = surfaceFormsSenses.get(surfaceForm);

			List<SurfaceFormSenseScore<T, U>> sFSS = new ArrayList<>();
			for (U sense : sFSenses) { // get the score for each sense
				Vertex v = Graphs.vertexByUri(subgraph, sense.fullUri());
				double score = (v == null) ? -1 : vertexScorer.getVertexScore(v);
				sFSS.add(new SurfaceFormSenseScore<T, U>(surfaceForm, sense, score));
			}
			// TODO maybe this should be changed to ascending order

			Collections.sort(sFSS, descendingScoreComparator);
			int toIndex = k > sFSS.size() ? sFSS.size() : k;
			senseScores.put(surfaceForm, sFSS.subList(0, toIndex));
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
			SurfaceFormSenseScore<T, U> highestScoreSense = Collections.max(sFSScores, descendingScoreComparator);
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
		return getClass().getSimpleName() + "[ graphType: " + graphType.getDirection() + " ]";
	}
}
