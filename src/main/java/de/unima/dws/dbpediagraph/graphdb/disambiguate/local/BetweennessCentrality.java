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
import de.unima.dws.dbpediagraph.graphdb.disambiguate.LocalGraphDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.WeightedSense;

/**
 * @author Bernhard Schäfer
 */
// TODO evaluate GraphStream
// https://github.com/graphstream/gs-algo/blob/master/src/org/graphstream/algorithm/BetweennessCentrality.java
// http://www.javacodegeeks.com/2013/07/mini-search-engine-just-the-basics-using-neo4j-crawler4j-graphstream-and-encog.html
public enum BetweennessCentrality implements LocalGraphDisambiguator {
	DIRECTED(GraphType.DIRECTED_GRAPH), UNDIRECTED(GraphType.UNDIRECTED_GRAPH);

	public static BetweennessCentrality forGraphType(GraphType graphType) {
		switch (graphType) {
		case DIRECTED_GRAPH:
			return DIRECTED;
		case UNDIRECTED_GRAPH:
			return UNDIRECTED;
		default:
			throw new IllegalArgumentException();
		}
	}

	private final GraphType graphType;

	private BetweennessCentrality(GraphType graphType) {
		this.graphType = graphType;
	}

	@Override
	public List<WeightedSense> disambiguate(Collection<String> senses, Graph subgraph) {
		GraphJung<Graph> graphJung = Graphs.asGraphJung(graphType, subgraph);
		edu.uci.ics.jung.algorithms.scoring.BetweennessCentrality<Vertex, Edge> betweenness = new edu.uci.ics.jung.algorithms.scoring.BetweennessCentrality<Vertex, Edge>(
				graphJung);
		int vertCount = Graphs.numberOfVertices(subgraph);

		List<WeightedSense> wSenses = new ArrayList<>();
		for (String sense : senses) {
			double score = betweenness.getVertexScore(Graphs.vertexByUri(subgraph, sense));
			double normalizedScore = score / ((vertCount - 1) * (vertCount - 2));
			wSenses.add(new WeightedSense(sense, normalizedScore));
		}
		return wSenses;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " (graphType: " + graphType + " )";
	}
}
