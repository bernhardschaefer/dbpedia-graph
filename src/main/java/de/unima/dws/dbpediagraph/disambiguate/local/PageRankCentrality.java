package de.unima.dws.dbpediagraph.disambiguate.local;

import java.util.Map;

import com.tinkerpop.blueprints.*;
import com.tinkerpop.blueprints.oupls.jung.GraphJung;

import de.unima.dws.dbpediagraph.disambiguate.AbstractLocalGraphDisambiguator;
import de.unima.dws.dbpediagraph.disambiguate.LocalGraphDisambiguator;
import de.unima.dws.dbpediagraph.graph.GraphType;
import de.unima.dws.dbpediagraph.model.Sense;
import de.unima.dws.dbpediagraph.model.SurfaceForm;
import de.unima.dws.dbpediagraph.weights.EdgeWeights;
import edu.uci.ics.jung.algorithms.scoring.*;

/**
 * @author Bernhard Schäfer
 */
public class PageRankCentrality<T extends SurfaceForm, U extends Sense> extends AbstractLocalGraphDisambiguator<T, U>
		implements LocalGraphDisambiguator<T, U> {

	/** Default Iterations value from "The PageRank Citation Ranking: Bringing Order to the Web" */
	private static final int DEFAULT_ITERATIONS = 52;
	/** damping factor d is normally 0.85. According to {@link PageRank}, alpha is used as (1-d); thus alpha = 0.15 */
	private static final double DEFAULT_ALPHA = 0.15;

	private final double alpha;
	private final int iterations;

	public PageRankCentrality(GraphType graphType, EdgeWeights graphWeights, double alpha, int iterations) {
		super(graphType, graphWeights);
		this.alpha = alpha;
		this.iterations = iterations;
	}

	public PageRankCentrality(GraphType graphType, EdgeWeights graphWeights) {
		this(graphType, graphWeights, DEFAULT_ALPHA, DEFAULT_ITERATIONS);
	}

	@Override
	protected VertexScorer<Vertex, Double> getVertexScorer(Graph subgraph, Map<Vertex, Double> vertexPriors) {
		GraphJung<Graph> graphJung = new GraphJung<Graph>(subgraph); // PageRank values overflow with undirected graph
		PageRankWithPriors<Vertex, Edge> pageRank = new PageRank<Vertex, Edge>(graphJung, edgeWeights, alpha);
		return new PRVertexScorer(pageRank, subgraph, iterations);
	}

	@Override
	public String toString() {
		return new StringBuilder(super.toString()).append(" (alpha: ").append(alpha).append(", iterations: ")
				.append(iterations).append(")").toString();
	}

}
