package de.unima.dws.dbpediagraph.disambiguate.local;

import java.util.Map;

import org.apache.commons.collections15.TransformerUtils;

import com.tinkerpop.blueprints.*;
import com.tinkerpop.blueprints.oupls.jung.GraphJung;

import de.unima.dws.dbpediagraph.disambiguate.AbstractLocalGraphDisambiguator;
import de.unima.dws.dbpediagraph.disambiguate.LocalGraphDisambiguator;
import de.unima.dws.dbpediagraph.graph.GraphType;
import de.unima.dws.dbpediagraph.graph.Graphs;
import de.unima.dws.dbpediagraph.model.Sense;
import de.unima.dws.dbpediagraph.model.SurfaceForm;
import de.unima.dws.dbpediagraph.weights.EdgeWeights;
import edu.uci.ics.jung.algorithms.scoring.*;

/**
 * @author Bernhard Schäfer
 */
public class PageRankWithPriorsCentrality<T extends SurfaceForm, U extends Sense> extends
		AbstractLocalGraphDisambiguator<T, U> implements LocalGraphDisambiguator<T, U> {

	/** Use less iterations than in official paper to make sure prior does not loose all influence. */
	private static final int DEFAULT_ITERATIONS = 20;
	private static final double DEFAULT_ALPHA = 0;

	private final double alpha;
	private final int iterations;

	public PageRankWithPriorsCentrality(GraphType graphType, EdgeWeights graphWeights, double alpha, int iterations) {
		super(graphType, graphWeights);
		this.alpha = alpha;
		this.iterations = iterations;
	}

	public PageRankWithPriorsCentrality(GraphType graphType, EdgeWeights graphWeights) {
		this(graphType, graphWeights, DEFAULT_ALPHA, DEFAULT_ITERATIONS);
	}

	@Override
	protected VertexScorer<Vertex, Double> getVertexScorer(Graph subgraph, Map<Vertex, Double> vertexPriors) {
		GraphJung<Graph> graphJung = Graphs.asGraphJung(graphType, subgraph);
		PageRankWithPriors<Vertex, Edge> pageRank = new PageRankWithPriors<Vertex, Edge>(graphJung, edgeWeights,
				TransformerUtils.mapTransformer(vertexPriors), alpha);
		return new PRVertexScorer(pageRank, subgraph, iterations);
	}

	@Override
	public String toString() {
		return new StringBuilder(super.toString()).append(" (alpha: ").append(alpha).append(", iterations: ")
				.append(iterations).append(")").toString();
	}

}