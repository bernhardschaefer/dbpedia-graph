package de.unima.dws.dbpediagraph.disambiguate.local;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.oupls.jung.GraphJung;

import de.unima.dws.dbpediagraph.disambiguate.AbstractLocalGraphDisambiguator;
import de.unima.dws.dbpediagraph.disambiguate.LocalGraphDisambiguator;
import de.unima.dws.dbpediagraph.graph.GraphType;
import de.unima.dws.dbpediagraph.graph.Graphs;
import de.unima.dws.dbpediagraph.model.*;
import de.unima.dws.dbpediagraph.util.CollectionUtils;
import edu.uci.ics.jung.algorithms.scoring.HITS;
import edu.uci.ics.jung.algorithms.scoring.HITS.Scores;
import edu.uci.ics.jung.algorithms.scoring.VertexScorer;

/**
 * @author Bernhard Schäfer
 */
public class HITSCentrality<T extends SurfaceForm, U extends Sense> extends AbstractLocalGraphDisambiguator<T, U>
		implements LocalGraphDisambiguator<T, U> {
	/**
	 * Maintains hub and authority score information for a vertex.
	 */
	public static class HitsScores {
		/**
		 * The hub score for a vertex.
		 */
		public double hub;

		/**
		 * The authority score for a vertex.
		 */
		public double authority;

		/**
		 * Creates an instance with the specified hub and authority score.
		 */
		public HitsScores(double hub, double authority) {
			this.hub = hub;
			this.authority = authority;
		}

		public HitsScores(HitsScores scores) {
			this(scores.hub, scores.authority);
		}

		@Override
		public String toString() {
			return String.format("[h:%.4f,a:%.4f]", this.hub, this.authority);
		}
	}

	class HITSVertexScorer implements VertexScorer<Vertex, Double> {

		private final HITS<Vertex, Edge> hits;

		// private final Map<Vertex, HitsScores> hitsScores;

		public HITSVertexScorer(Graph subgraph) {
			GraphJung<Graph> graphJung = Graphs.asGraphJung(graphType, subgraph);
			hits = new HITS<Vertex, Edge>(graphJung, alpha);
			hits.acceptDisconnectedGraph(true);
			hits.setMaxIterations(iterations);
			hits.evaluate();
			// hitsScores = calculateHitsScores(subgraph);
		}

		@Override
		public Double getVertexScore(Vertex v) {
			Scores scores = hits.getVertexScore(v);
			// HitsScores scores = hitsScores.get(v);
			double authority = CollectionUtils.iterableItemCount(Graphs.connectedEdges(v, graphType)) != 0 ? scores.authority
					: 0;
			return authority;
		}
	}

	private static final int DEFAULT_ITERATIONS = 10;

	private static final double DEFAULT_ALPHA = 0;

	private static final double INITIAL_SCORE = 1;

	@Deprecated
	private static Map<Vertex, HitsScores> createInitialScores(Collection<Vertex> vertices, double initialScore) {
		Map<Vertex, HitsScores> scores = new HashMap<>();
		for (Vertex v : vertices)
			scores.put(v, new HitsScores(initialScore, initialScore));
		return scores;
	}

	private final double alpha;

	private final int iterations;

	private String name;

	public HITSCentrality(GraphType graphType, double alpha, int iterations, ModelFactory<T, U> factory) {
		super(graphType, factory);
		this.alpha = alpha;
		this.iterations = iterations;
	}

	public HITSCentrality(GraphType graphType, ModelFactory<T, U> factory) {
		this(graphType, DEFAULT_ALPHA, DEFAULT_ITERATIONS, factory);
	}

	@Deprecated
	private double authority(Vertex v, Map<Vertex, HitsScores> vScores) {
		double authority = 0;
		for (Vertex adjacentVertex : v.getVertices(graphType.getDirection()))
			authority += vScores.get(adjacentVertex).hub;
		return authority;
	}

	@SuppressWarnings("unused")
	@Deprecated
	private Map<Vertex, HitsScores> calculateHitsScores(Graph subgraph) {
		Collection<Vertex> graphVertices = CollectionUtils.iterableToCollection(subgraph.getVertices());

		Map<Vertex, HitsScores> scores = createInitialScores(graphVertices, INITIAL_SCORE);

		for (int i = 0; i < iterations; i++) {
			Map<Vertex, HitsScores> oldScores = CollectionUtils.deepCopy(scores);
			for (Vertex v : graphVertices) {
				scores.get(v).authority = authority(v, oldScores);
				scores.get(v).hub = hub(v, oldScores);
			}
			normalize(scores);
		}

		return scores;
	}

	@Override
	protected VertexScorer<Vertex, Double> getVertexScorer(Graph subgraph) {
		return new HITSVertexScorer(subgraph);
	}

	@Deprecated
	private double hub(Vertex v, Map<Vertex, HitsScores> vScores) {
		double hub = 0;
		for (Vertex adjacentVertex : v.getVertices(graphType.getDirection()))
			hub += vScores.get(adjacentVertex).authority;
		return hub;
	}

	@Deprecated
	private void normalize(Map<Vertex, HitsScores> scores) {
		double authSum = 0, hubSum = 0;
		for (Entry<Vertex, HitsScores> entry : scores.entrySet()) {
			authSum += entry.getValue().authority;
			hubSum += entry.getValue().hub;
		}
		for (Entry<Vertex, HitsScores> entry : scores.entrySet()) {
			entry.getValue().authority = entry.getValue().authority / authSum;
			entry.getValue().hub = entry.getValue().hub / hubSum;
		}
	}

	@Override
	public String toString() {
		if (name == null)
			name = new StringBuilder(super.toString()).append(" (alpha: ").append(alpha).append(", iterations: ")
					.append(iterations).append(")").toString();
		return name;
	}
}