package de.unima.dws.dbpediagraph.graphdb.disambiguate.local;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.GraphUtil;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.LocalDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.WeightedSense;
import de.unima.dws.dbpediagraph.graphdb.wrapper.GraphJungUndirected;
import edu.uci.ics.jung.algorithms.scoring.PageRank;

public class PageRankCentrality implements LocalDisambiguator {
	private static final int DEFAULT_ITERATIONS = 10;

	private final double alpha;
	private final int iterations;

	private String name;

	public PageRankCentrality(double alpha) {
		this.alpha = alpha;
		this.iterations = DEFAULT_ITERATIONS;
	}

	public PageRankCentrality(double alpha, int iterations) {
		this.alpha = alpha;
		this.iterations = iterations;
	}

	@Override
	public List<WeightedSense> disambiguate(Collection<String> senses, Graph subgraph) {
		PageRank<Vertex, Edge> pageRank = new PageRank<Vertex, Edge>(new GraphJungUndirected(subgraph), alpha);
		pageRank.setMaxIterations(iterations);
		pageRank.evaluate();

		// VertexProgram pr = PageRankProgram.create().alpha(alpha).iterations(iterations).build();
		// GraphComputer computer = new SerialGraphComputer(subgraph, pr, Isolation.BSP);
		// computer.execute();
		// VertexMemory vertexMemory = computer.getVertexMemory();

		List<WeightedSense> wSenses = new ArrayList<>();
		for (String sense : senses) {
			Vertex vertex = GraphUtil.getVertexByUri(subgraph, sense);
			double rank = pageRank.getVertexScore(vertex);

			// double rank = vertexMemory.getProperty(vertex, PageRankProgram.PAGE_RANK);
			// double edgeCount = vertexMemory.getProperty(vertex, PageRankProgram.EDGE_COUNT);
			wSenses.add(new WeightedSense(sense, rank));
		}
		return wSenses;
	}

	@Override
	public String toString() {
		if (name == null) {
			name = new StringBuilder(this.getClass().getSimpleName()).append(" (alpha: ").append(alpha)
					.append(", iterations: ").append(iterations).append(")").toString();
		}
		return name;
	}

}
