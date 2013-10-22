package de.unima.dws.dbpediagraph.graphdb.disambiguate.local;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.oupls.jung.GraphJung;

import de.unima.dws.dbpediagraph.graphdb.GraphType;
import de.unima.dws.dbpediagraph.graphdb.Graphs;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.LocalDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.WeightedSense;
import de.unima.dws.dbpediagraph.graphdb.util.CollectionUtils;
import edu.uci.ics.jung.algorithms.scoring.HITS;
import edu.uci.ics.jung.algorithms.scoring.HITS.Scores;

/**
 * @author Bernhard Schäfer
 */
public class HITSCentrality implements LocalDisambiguator {
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

	private static final int DEFAULT_ITERATIONS = 10;

	private static final double DEFAULT_ALPHA = 0;

	private static final double INITIAL_SCORE = 1;

	private static Map<Vertex, HitsScores> createInitialScores(Collection<Vertex> vertices, double initialScore) {
		Map<Vertex, HitsScores> scores = new HashMap<>();
		for (Vertex v : vertices)
			scores.put(v, new HitsScores(initialScore, initialScore));
		return scores;
	}

	public static HITSCentrality defaultForGraphType(GraphType graphType) {
		switch (graphType) {
		case DIRECTED_GRAPH:
			return new HITSCentrality(GraphType.DIRECTED_GRAPH, DEFAULT_ALPHA, DEFAULT_ITERATIONS);
		case UNDIRECTED_GRAPH:
			return new HITSCentrality(GraphType.UNDIRECTED_GRAPH, DEFAULT_ALPHA, DEFAULT_ITERATIONS);
		default:
			throw new IllegalArgumentException();
		}
	}

	private final GraphType graphType;

	private final double alpha;

	private final int iterations;

	private String name;

	public HITSCentrality(GraphType graphType, double alpha, int iterations) {
		this.graphType = graphType;
		this.alpha = alpha;
		this.iterations = iterations;
	}

	private double authority(Vertex v, Map<Vertex, HitsScores> vScores) {
		Direction direction = null;
		switch (graphType) {
		case DIRECTED_GRAPH:
			direction = Direction.IN;
		case UNDIRECTED_GRAPH:
			direction = Direction.BOTH;
		}

		double authority = 0;
		for (Vertex adjacentVertex : v.getVertices(direction))
			authority += vScores.get(adjacentVertex).hub;
		return authority;
	}

	@SuppressWarnings("unused")
	private Map<Vertex, HitsScores> calculateHitsScores(Graph subgraph) {
		Collection<Vertex> graphVertices = CollectionUtils.iterToCollection(subgraph.getVertices());

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
	public List<WeightedSense> disambiguate(Collection<String> senses, Graph subgraph) {
		GraphJung<Graph> graphJung = Graphs.asGraphJung(graphType, subgraph);
		HITS<Vertex, Edge> hits = new HITS<Vertex, Edge>(graphJung, alpha);
		hits.acceptDisconnectedGraph(true);
		hits.setMaxIterations(iterations);
		hits.evaluate();

		// Map<Vertex, Scores> scores = calculateHitsScores(subgraph);

		// List<Vertex> senseVertices = Graphs.verticesByUri(subgraph, senses);
		List<WeightedSense> wUris = new ArrayList<>();
		// for (Vertex v : senseVertices)
		for (String uri : senses) {
			Vertex v = Graphs.vertexByUri(subgraph, uri);
			Scores scores = hits.getVertexScore(v);
			// wUris.add(new WeightedSense(v.getProperty(GraphConfig.URI_PROPERTY).toString(),
			// scores.get(v).authority));
			double authority = CollectionUtils.getIterItemCount(Graphs.connectedEdges(v, graphType).iterator()) != 0 ? scores.authority
					: 0;
			wUris.add(new WeightedSense(uri, authority));
		}
		return wUris;
	}

	private double hub(Vertex v, Map<Vertex, HitsScores> vScores) {
		Direction direction = null;
		switch (graphType) {
		case DIRECTED_GRAPH:
			direction = Direction.OUT;
		case UNDIRECTED_GRAPH:
			direction = Direction.BOTH;
		}

		double hub = 0;
		for (Vertex adjacentVertex : v.getVertices(direction))
			hub += vScores.get(adjacentVertex).authority;
		return hub;
	}

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
			name = new StringBuilder(this.getClass().getSimpleName()).append(" (alpha: ").append(alpha)
					.append(", iterations: ").append(iterations).append(", graphType: ").append(graphType).append(")")
					.toString();
		return name;
	}
}
