package de.unima.dws.dbpediagraph.disambiguate.local;

import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.TransformerUtils;

import com.tinkerpop.blueprints.*;

import de.unima.dws.dbpediagraph.disambiguate.AbstractLocalGraphDisambiguator;
import de.unima.dws.dbpediagraph.disambiguate.LocalGraphDisambiguator;
import de.unima.dws.dbpediagraph.graph.GraphType;
import de.unima.dws.dbpediagraph.graph.Graphs;
import de.unima.dws.dbpediagraph.model.Sense;
import de.unima.dws.dbpediagraph.model.SurfaceForm;
import de.unima.dws.dbpediagraph.weights.EdgeWeights;
import edu.uci.ics.jung.algorithms.scoring.HITS.Scores;
import edu.uci.ics.jung.algorithms.scoring.*;

/**
 * @author Bernhard Schäfer
 */
public class HITSWithPriorsCentrality<T extends SurfaceForm, U extends Sense> extends
		AbstractLocalGraphDisambiguator<T, U> implements LocalGraphDisambiguator<T, U> {

	/** Use less iterations than in official paper to make sure prior does not loose all influence. */
	private static final int DEFAULT_ITERATIONS = 10;

	private static final double DEFAULT_ALPHA = 0;

	private final double alpha;
	private final int iterations;

	public HITSWithPriorsCentrality(GraphType graphType, EdgeWeights graphWeights, double alpha, int iterations) {
		super(graphType, graphWeights);
		this.alpha = alpha;
		this.iterations = iterations;
	}

	public HITSWithPriorsCentrality(GraphType graphType, EdgeWeights graphWeights) {
		this(graphType, graphWeights, DEFAULT_ALPHA, DEFAULT_ITERATIONS);
	}

	private static Transformer<Vertex, Scores> toHitsPriors(Map<Vertex, Double> vertexPriors) {
		Map<Vertex, Scores> hitsPriors = new HashMap<>();
		for (Entry<Vertex, Double> entry : vertexPriors.entrySet()) {
			// TODO should prior be used with hub instead? or both?
			double hub = 0;
			Double authority = entry.getValue();
			hitsPriors.put(entry.getKey(), new Scores(hub, authority));
		}
		return new ScoresTransformer(hitsPriors);
	}

	@Override
	protected VertexScorer<Vertex, Double> getVertexScorer(Graph subgraph, Map<Vertex, Double> vertexPriors) {
		HITSWithPriors<Vertex, Edge> hits = new HITSWithPriors<Vertex, Edge>(Graphs.asGraphJung(graphType, subgraph),
				edgeWeights, toHitsPriors(vertexPriors), alpha);
		return new HITSVertexScorer(hits, iterations);
	}

	@Override
	public String toString() {
		return new StringBuilder(super.toString()).append(" (alpha: ").append(alpha).append(", iterations: ")
				.append(iterations).append(")").toString();
	}

	/**
	 * Decorates a transformer with zero scores for non existing vertices.
	 */
	static class ScoresTransformer implements Transformer<Vertex, Scores> {
		private Transformer<Vertex, Scores> transformer;

		public ScoresTransformer(Map<Vertex, Scores> hitsPriors) {
			transformer = TransformerUtils.mapTransformer(hitsPriors);
		}

		@Override
		public Scores transform(Vertex v) {
			Scores scores = transformer.transform(v);
			return scores != null ? scores : new Scores(0, 0);
		}

	}
}