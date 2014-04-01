package de.unima.dws.dbpediagraph.disambiguate.local;

import java.util.Map;

import com.tinkerpop.blueprints.*;
import com.tinkerpop.blueprints.oupls.jung.GraphJung;

import de.unima.dws.dbpediagraph.disambiguate.AbstractLocalGraphDisambiguator;
import de.unima.dws.dbpediagraph.disambiguate.LocalGraphDisambiguator;
import de.unima.dws.dbpediagraph.graph.GraphType;
import de.unima.dws.dbpediagraph.graph.Graphs;
import de.unima.dws.dbpediagraph.model.Sense;
import de.unima.dws.dbpediagraph.model.SurfaceForm;
import de.unima.dws.dbpediagraph.weights.EdgeWeights;
import edu.uci.ics.jung.algorithms.scoring.VertexScorer;

/**
 * Betweenness Centrality implementation as described in Navigli & Lapata (2010).
 * 
 * @author Bernhard Schäfer
 */
// TODO evaluate GraphStream
// https://github.com/graphstream/gs-algo/blob/master/src/org/graphstream/algorithm/BetweennessCentrality.java
// http://www.javacodegeeks.com/2013/07/mini-search-engine-just-the-basics-using-neo4j-crawler4j-graphstream-and-encog.html
public class BetweennessCentrality<T extends SurfaceForm, U extends Sense> extends
		AbstractLocalGraphDisambiguator<T, U> implements LocalGraphDisambiguator<T, U> {

	class BetweennessVertexScorer implements VertexScorer<Vertex, Double> {

		private final edu.uci.ics.jung.algorithms.scoring.BetweennessCentrality<Vertex, Edge> betweenness;
		private final int verticesCount;

		public BetweennessVertexScorer(Graph subgraph) {
			GraphJung<Graph> graphJung = Graphs.asGraphJung(graphType, subgraph);
			betweenness = new edu.uci.ics.jung.algorithms.scoring.BetweennessCentrality<Vertex, Edge>(graphJung,
					edgeWeights);
			verticesCount = Graphs.verticesCount(subgraph);
		}

		@Override
		public Double getVertexScore(Vertex v) {
			double score = betweenness.getVertexScore(v);
			double normalizedScore = score / ((verticesCount - 1) * (verticesCount - 2));
			return normalizedScore;
		}

	}

	public BetweennessCentrality(GraphType graphType, EdgeWeights graphWeights) {
		super(graphType, graphWeights);
	}

	@Override
	protected VertexScorer<Vertex, Double> getVertexScorer(Graph subgraph, Map<Vertex, Double> vertexPriors) {
		return new BetweennessVertexScorer(subgraph);
	}

}
