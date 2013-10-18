package de.unima.dws.dbpediagraph.graphdb.disambiguate.local;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.Graphs;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.LocalDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.WeightedSense;
import de.unima.dws.dbpediagraph.graphdb.wrapper.GraphJungUndirected;

//TODO evaluate GraphStream https://github.com/graphstream/gs-algo/blob/master/src/org/graphstream/algorithm/BetweennessCentrality.java
// http://www.javacodegeeks.com/2013/07/mini-search-engine-just-the-basics-using-neo4j-crawler4j-graphstream-and-encog.html
public class BetweennessCentrality implements LocalDisambiguator {

	@Override
	public List<WeightedSense> disambiguate(Collection<String> senses, Graph subgraph) {
		edu.uci.ics.jung.algorithms.scoring.BetweennessCentrality<Vertex, Edge> betweenness = new edu.uci.ics.jung.algorithms.scoring.BetweennessCentrality<Vertex, Edge>(
				new GraphJungUndirected(subgraph));
		int vertCount = Graphs.getNumberOfVertices(subgraph);

		List<WeightedSense> wSenses = new ArrayList<>();
		for (String sense : senses) {
			double score = betweenness.getVertexScore(Graphs.getVertexByUri(subgraph, sense));
			double normalizedScore = score / ((vertCount - 1) * (vertCount - 2));
			wSenses.add(new WeightedSense(sense, normalizedScore));
		}
		return wSenses;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}
