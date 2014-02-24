package de.unima.dws.dbpediagraph.disambiguate.local;

import java.util.Map;

import com.tinkerpop.blueprints.*;

import de.unima.dws.dbpediagraph.disambiguate.AbstractLocalGraphDisambiguator;
import de.unima.dws.dbpediagraph.disambiguate.LocalGraphDisambiguator;
import de.unima.dws.dbpediagraph.graph.GraphType;
import de.unima.dws.dbpediagraph.graph.Graphs;
import de.unima.dws.dbpediagraph.model.Sense;
import de.unima.dws.dbpediagraph.model.SurfaceForm;
import de.unima.dws.dbpediagraph.weights.EdgeWeights;
import edu.uci.ics.jung.algorithms.scoring.HITS;
import edu.uci.ics.jung.algorithms.scoring.VertexScorer;

/**
 * @author Bernhard Schäfer
 */
public class HITSCentrality<T extends SurfaceForm, U extends Sense> extends AbstractLocalGraphDisambiguator<T, U>
		implements LocalGraphDisambiguator<T, U> {

	/** According to the founder of HITS (Kleinberg, 1999), 20 is sufficient for stable values in most cases. */
	private static final int DEFAULT_ITERATIONS = 20;

	private static final double DEFAULT_ALPHA = 0;

	private final double alpha;
	private final int iterations;

	public HITSCentrality(GraphType graphType, EdgeWeights graphWeights, double alpha, int iterations) {
		super(graphType, graphWeights);
		this.alpha = alpha;
		this.iterations = iterations;
	}

	public HITSCentrality(GraphType graphType, EdgeWeights graphWeights) {
		this(graphType, graphWeights, DEFAULT_ALPHA, DEFAULT_ITERATIONS);
	}

	@Override
	protected VertexScorer<Vertex, Double> getVertexScorer(Graph subgraph, Map<Vertex, Double> vertexPriors) {
		HITS<Vertex, Edge> hits = new HITS<Vertex, Edge>(Graphs.asGraphJung(graphType, subgraph), edgeWeights, alpha);
		return new HITSVertexScorer(hits, iterations);
	}

	@Override
	public String toString() {
		return new StringBuilder(super.toString()).append(" (alpha: ").append(alpha).append(", iterations: ")
				.append(iterations).append(")").toString();
	}

}
