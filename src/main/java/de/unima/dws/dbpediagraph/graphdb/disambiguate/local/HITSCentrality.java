package de.unima.dws.dbpediagraph.graphdb.disambiguate.local;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.oupls.jung.GraphJung;

import de.unima.dws.dbpediagraph.graphdb.GraphType;
import de.unima.dws.dbpediagraph.graphdb.Graphs;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.LocalDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.WeightedSense;
import edu.uci.ics.jung.algorithms.scoring.HITS;
import edu.uci.ics.jung.algorithms.scoring.HITS.Scores;

/**
 * @author Bernhard Schäfer
 */
public class HITSCentrality implements LocalDisambiguator {
	private static final int DEFAULT_ITERATIONS = 10;
	private static final double DEFAULT_ALPHA = 0;

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

	@Override
	public List<WeightedSense> disambiguate(Collection<String> senses, Graph subgraph) {
		GraphJung<Graph> graphJung = Graphs.asGraphJung(graphType, subgraph);
		HITS<Vertex, Edge> hits = new HITS<Vertex, Edge>(graphJung, alpha);
		hits.acceptDisconnectedGraph(true);
		hits.setMaxIterations(iterations);
		hits.evaluate();

		List<WeightedSense> wUris = new ArrayList<>();
		for (String uri : senses) {
			Scores scores = hits.getVertexScore(Graphs.vertexByUri(subgraph, uri));
			wUris.add(new WeightedSense(uri, scores.authority));
		}
		return wUris;
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
