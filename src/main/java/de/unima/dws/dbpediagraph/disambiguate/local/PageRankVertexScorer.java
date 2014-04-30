package de.unima.dws.dbpediagraph.disambiguate.local;

import com.tinkerpop.blueprints.*;

import de.unima.dws.dbpediagraph.graph.Graphs;
import edu.uci.ics.jung.algorithms.scoring.PageRankWithPriors;
import edu.uci.ics.jung.algorithms.scoring.VertexScorer;

/**
 * PageRank Vertex Scorer as helper class for PageRank centrality algorithms.
 * 
 * @author Bernhard Schäfer
 */
class PRVertexScorer implements VertexScorer<Vertex, Double> {
	private final double scoreSum;

	private final PageRankWithPriors<Vertex, Edge> pageRank;

	public PRVertexScorer(PageRankWithPriors<Vertex, Edge> pageRank, Graph subgraph, int iterations) {
		this.pageRank = pageRank;
		pageRank.setMaxIterations(iterations);
		pageRank.evaluate();

		scoreSum = calculateScoreSum(pageRank, subgraph);

		// VertexProgram pr = PageRankProgram.create().alpha(alpha).iterations(iterations).build();
		// GraphComputer computer = new SerialGraphComputer(subgraph, pr, Isolation.BSP);
		// computer.execute();
		// VertexMemory vertexMemory = computer.getVertexMemory();
	}

	private static double calculateScoreSum(PageRankWithPriors<Vertex, Edge> pageRank, Graph subgraph) {
		double scoreSum = 0;
		for (Vertex v : subgraph.getVertices())
			if (Graphs.vertexHasNeighbours(v))
				scoreSum += pageRank.getVertexScore(v);
		return scoreSum;
	}

	@Override
	public Double getVertexScore(Vertex v) {
		if (scoreSum == 0) // shortcut to prevent division by zero NaN
			return 0.0;
		if (Graphs.vertexHasNoNeighbours(v)) // unlike original PageRank, we want zero score for unconnected vertices
			return 0.0;
		// double rank = vertexMemory.getProperty(vertex, PageRankProgram.PAGE_RANK);
		// double edgeCount = vertexMemory.getProperty(vertex, PageRankProgram.EDGE_COUNT);
		return pageRank.getVertexScore(v) / scoreSum;
	}

}
