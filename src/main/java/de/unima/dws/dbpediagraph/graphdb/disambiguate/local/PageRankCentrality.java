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
import edu.uci.ics.jung.algorithms.scoring.PageRank;

/**
 * @author Bernhard Schäfer
 */
public class PageRankCentrality implements LocalDisambiguator {
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
	public List<WeightedSense> disambiguate(Collection<String> senses, Graph subgraph) {
		GraphJung<Graph> graphJung = Graphs.asGraphJung(graphType, subgraph);
		PageRank<Vertex, Edge> pageRank = new PageRank<Vertex, Edge>(graphJung, alpha);
		pageRank.setMaxIterations(iterations);
		pageRank.evaluate();

		// VertexProgram pr = PageRankProgram.create().alpha(alpha).iterations(iterations).build();
		// GraphComputer computer = new SerialGraphComputer(subgraph, pr, Isolation.BSP);
		// computer.execute();
		// VertexMemory vertexMemory = computer.getVertexMemory();

		List<WeightedSense> wSenses = new ArrayList<>();

		double scoreSum = calculateScoreSum(pageRank, subgraph);
		for (String sense : senses) {
			Vertex vertex = Graphs.vertexByUri(subgraph, sense);
			double rank = pageRank.getVertexScore(vertex) / scoreSum;

			// double rank = vertexMemory.getProperty(vertex, PageRankProgram.PAGE_RANK);
			// double edgeCount = vertexMemory.getProperty(vertex, PageRankProgram.EDGE_COUNT);
			wSenses.add(new WeightedSense(sense, rank));
		}
		return wSenses;
	}

	@Override
	public String toString() {
		if (name == null)
			name = new StringBuilder(this.getClass().getSimpleName()).append(" (alpha: ").append(alpha)
					.append(", iterations: ").append(iterations).append(")").toString();
		return name;
	}

}
