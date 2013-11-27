package de.unima.dws.dbpediagraph.disambiguate.local;

import com.tinkerpop.blueprints.*;
import com.tinkerpop.blueprints.oupls.jung.GraphJung;

import de.unima.dws.dbpediagraph.disambiguate.AbstractLocalGraphDisambiguator;
import de.unima.dws.dbpediagraph.disambiguate.LocalGraphDisambiguator;
import de.unima.dws.dbpediagraph.graph.GraphType;
import de.unima.dws.dbpediagraph.graph.Graphs;
import de.unima.dws.dbpediagraph.model.Sense;
import de.unima.dws.dbpediagraph.model.SurfaceForm;
import de.unima.dws.dbpediagraph.weights.GraphWeights;
import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.algorithms.scoring.VertexScorer;

/**
 * @author Bernhard Schäfer
 */
public class PageRankCentrality<T extends SurfaceForm, U extends Sense> extends AbstractLocalGraphDisambiguator<T, U>
		implements LocalGraphDisambiguator<T, U> {

	private static final int DEFAULT_ITERATIONS = 10;
	private static final double DEFAULT_ALPHA = 0;

	private final double alpha;
	private final int iterations;

	public PageRankCentrality(GraphType graphType, GraphWeights graphWeights, double alpha, int iterations) {
		super(graphType, graphWeights);
		this.alpha = alpha;
		this.iterations = iterations;
	}

	public PageRankCentrality(GraphType graphType, GraphWeights graphWeights) {
		this(graphType, graphWeights, DEFAULT_ALPHA, DEFAULT_ITERATIONS);
	}

	private double calculateScoreSum(PageRank<Vertex, Edge> pageRank, Graph subgraph) {
		double scoreSum = 0;
		Iterable<Vertex> vertices = subgraph.getVertices();
		for (Vertex v : vertices)
			scoreSum += pageRank.getVertexScore(v);
		return scoreSum;
	}

	class PRVertexScorer implements VertexScorer<Vertex, Double> {
		private final double scoreSum;
		private final PageRank<Vertex, Edge> pageRank;

		public PRVertexScorer(Graph subgraph) {
			GraphJung<Graph> graphJung = Graphs.asGraphJung(graphType, subgraph);
			pageRank = new PageRank<Vertex, Edge>(graphJung, alpha);
			pageRank.setMaxIterations(iterations);
			pageRank.evaluate();

			scoreSum = calculateScoreSum(pageRank, subgraph);

			// VertexProgram pr =
			// PageRankProgram.create().alpha(alpha).iterations(iterations).build();
			// GraphComputer computer = new SerialGraphComputer(subgraph, pr,
			// Isolation.BSP);
			// computer.execute();
			// VertexMemory vertexMemory = computer.getVertexMemory();
		}

		@Override
		public Double getVertexScore(Vertex v) {
			// double rank = vertexMemory.getProperty(vertex,
			// PageRankProgram.PAGE_RANK);
			// double edgeCount = vertexMemory.getProperty(vertex,
			// PageRankProgram.EDGE_COUNT);
			return pageRank.getVertexScore(v) / scoreSum;
		}

	}

	@Override
	protected VertexScorer<Vertex, Double> getVertexScorer(Graph subgraph) {
		return new PRVertexScorer(subgraph);
	}

	@Override
	public String toString() {
		return new StringBuilder(super.toString()).append(" (alpha: ").append(alpha).append(", iterations: ")
				.append(iterations).append(")").toString();
	}

}
