package de.unima.dws.dbpediagraph.graphdb.disambiguate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.Graphs;
import de.unima.dws.dbpediagraph.graphdb.model.ModelFactory;
import de.unima.dws.dbpediagraph.graphdb.model.ModelTransformer;
import de.unima.dws.dbpediagraph.graphdb.model.Sense;
import de.unima.dws.dbpediagraph.graphdb.model.SurfaceForm;
import de.unima.dws.dbpediagraph.graphdb.model.SurfaceFormSenseScore;
import edu.uci.ics.jung.algorithms.scoring.VertexScorer;

/**
 * Skeleton class which eases the implementation of {@link GraphDisambiguator}.
 * Subclasses only need to implement {@link #getVertexScorer(Graph)}.
 * 
 * @author Bernhard Schäfer
 * 
 */
public abstract class AbstractLocalGraphDisambiguator<T extends SurfaceForm, U extends Sense> implements
		LocalGraphDisambiguator<T, U> {
	private static final Logger logger = LoggerFactory.getLogger(AbstractLocalGraphDisambiguator.class);

	protected ModelFactory<T, U> factory;

	public AbstractLocalGraphDisambiguator(ModelFactory<T, U> factory) {
		this.factory = factory;
	}

	@Override
	public Map<T, List<SurfaceFormSenseScore<T, U>>> allSurfaceFormSensesScores(Map<T, List<U>> surfaceFormsSenses,
			Graph subgraph) {
		return bestK(surfaceFormsSenses, subgraph, Integer.MAX_VALUE);
	}

	@Override
	public Map<T, List<SurfaceFormSenseScore<T, U>>> bestK(Map<T, List<U>> surfaceFormsSenses, Graph subgraph, int k) {
		VertexScorer<Vertex, Double> vertexScorer = getVertexScorer(subgraph);

		Map<T, List<SurfaceFormSenseScore<T, U>>> senseScores = ModelTransformer.initializeScoresMapFromMap(
				surfaceFormsSenses, factory);

		// TODO iterate over surfaceFormsSenses and omit initializer
		for (T key : senseScores.keySet()) {
			List<SurfaceFormSenseScore<T, U>> sFSS = senseScores.get(key);
			for (SurfaceFormSenseScore<T, U> senseScore : sFSS) {
				Vertex v = Graphs.vertexByUri(subgraph, senseScore.sense().fullUri());
				double score = (v == null) ? -1 : vertexScorer.getVertexScore(v);
				senseScore.setScore(score);
			}
			Collections.sort(sFSS);
			Collections.reverse(sFSS);

			int toIndex = k > sFSS.size() ? sFSS.size() : k;
			senseScores.put(key, sFSS.subList(0, toIndex));
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
			SurfaceFormSenseScore<T, U> highestScoreSense = Collections.max(sFSScores);
			highestScores.add(highestScoreSense);
		}

		return highestScores;
	}

	/**
	 * This method is called in {@link #disambiguate(Collection, Graph)} to
	 * retrieve a score for each vertex corresponding to a sense.
	 * 
	 * @param subgraph
	 *            the subgraph that the vertices are contained in
	 * @return a {@link VertexScorer} implementation
	 */
	protected abstract VertexScorer<Vertex, Double> getVertexScorer(Graph subgraph);

}
