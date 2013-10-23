package de.unima.dws.dbpediagraph.graphdb.disambiguate.local;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.oupls.jung.GraphJung;

import de.unima.dws.dbpediagraph.graphdb.GraphType;
import de.unima.dws.dbpediagraph.graphdb.Graphs;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.AbstractLocalGraphDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.LocalGraphDisambiguator;
import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.algorithms.scoring.VertexScorer;

/**
 * @author Bernhard Schäfer
 */
public class PageRankCentrality extends AbstractLocalGraphDisambiguator implements LocalGraphDisambiguator {
	class PRVertexScorer implements VertexScorer<Vertex, Double> {
		private final double scoreSum;
		private final PageRank<Vertex, Edge> pageRank;

		public PRVertexScorer(Graph subgraph) {
			GraphJung<Graph> graphJung = Graphs.asGraphJung(graphType, subgraph);
			pageRank = new PageRank<Vertex, Edge>(graphJung, alpha);
			pageRank.setMaxIterations(iterations);
			pageRank.evaluate();

			scoreSum = calculateScoreSum(pageRank, subgraph);

			// VertexProgram pr = PageRankProgram.create().alpha(alpha).iterations(iterations).build();
			// GraphComputer computer = new SerialGraphComputer(subgraph, pr, Isolation.BSP);
			// computer.execute();
			// VertexMemory vertexMemory = computer.getVertexMemory();
		}

		@Override
		public Double getVertexScore(Vertex v) {
			// double rank = vertexMemory.getProperty(vertex, PageRankProgram.PAGE_RANK);
			// double edgeCount = vertexMemory.getProperty(vertex, PageRankProgram.EDGE_COUNT);
			return pageRank.getVertexScore(v) / scoreSum;
		}

	}

	private static final int DEFAULT_ITERATIONS = 10;

	private static final double DEFAULT_ALPHA = 0;

	public static PageRankCentrality defaultForGraphType(GraphType graphType) {
		switch (graphType) {
		case DIRECTED_GRAPH:
			return new PageRankCentrality(GraphType.DIRECTED_GRAPH, DEFAULT_ALPHA, DEFAULT_ITERATIONS);
		case UNDIRECTED_GRAPH:
			return new PageRankCentrality(GraphType.UNDIRECTED_GRAPH, DEFAULT_ALPHA, DEFAULT_ITERATIONS);
		default:
			throw new IllegalArgumentException();
		}
	}

	private final GraphType graphType;

	private final double alpha;
	private final int iterations;

	private String name;

	public PageRankCentrality(GraphType graphType, double alpha, int iterations) {
		this.graphType = graphType;
		this.alpha = alpha;
		this.iterations = iterations;
	}

	private double calculateScoreSum(PageRank<Vertex, Edge> pageRank, Graph subgraph) {
		double scoreSum = 0;
		Iterable<Vertex> vertices = subgraph.getVertices();
		for (Vertex v : vertices)
			scoreSum += pageRank.getVertexScore(v);
		return scoreSum;
	}

	@Override
	protected VertexScorer<Vertex, Double> getVertexScorer(Graph subgraph) {
		return new PRVertexScorer(subgraph);
	}

	@Override
	public String toString() {
		if (name == null)
			name = new StringBuilder(this.getClass().getSimpleName()).append(" (alpha: ").append(alpha)
					.append(", iterations: ").append(iterations).append(")").toString();
		return name;
	}

}
