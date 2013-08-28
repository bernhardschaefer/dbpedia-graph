package de.unima.dws.dbpediagraph.graphdb.disambiguate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.oupls.jung.GraphJung;

import de.unima.dws.dbpediagraph.graphdb.GraphUtil;
import edu.uci.ics.jung.algorithms.scoring.PageRank;

public class PageRankCentrality implements LocalDisambiguator {
	private static final int DEFAULT_ITERATIONS = 100;

	private final double alpha;
	private final int iterations;

	public PageRankCentrality(double alpha) {
		this.alpha = alpha;
		this.iterations = DEFAULT_ITERATIONS;
	}

	public PageRankCentrality(double alpha, int iterations) {
		this.alpha = alpha;
		this.iterations = iterations;
	}

	@Override
	public List<WeightedUri> disambiguate(Collection<String> uris, Graph subgraph) {
		PageRank<Vertex, Edge> pageRank = new PageRank<Vertex, Edge>(new GraphJung<Graph>(subgraph), alpha);
		pageRank.setMaxIterations(iterations);
		pageRank.evaluate();

		List<WeightedUri> wUris = new ArrayList<>();
		for (String uri : uris) {
			double weight = pageRank.getVertexScore(GraphUtil.getVertexByUri(subgraph, uri));
			wUris.add(new WeightedUri(uri, weight));
		}
		return wUris;
	}

	@Override
	public LocalConnectivityMeasure getType() {
		return LocalConnectivityMeasure.PR;
	}

}
