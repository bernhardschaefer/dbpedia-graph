package de.unima.dws.dbpediagraph.graphdb.disambiguate.local;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.oupls.jung.GraphJung;

import de.unima.dws.dbpediagraph.graphdb.GraphType;
import de.unima.dws.dbpediagraph.graphdb.Graphs;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.AbstractLocalGraphDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.LocalGraphDisambiguator;
import edu.uci.ics.jung.algorithms.scoring.VertexScorer;

/**
 * @author Bernhard Schäfer
 */
// TODO evaluate GraphStream
// https://github.com/graphstream/gs-algo/blob/master/src/org/graphstream/algorithm/BetweennessCentrality.java
// http://www.javacodegeeks.com/2013/07/mini-search-engine-just-the-basics-using-neo4j-crawler4j-graphstream-and-encog.html
public class BetweennessCentrality extends AbstractLocalGraphDisambiguator implements LocalGraphDisambiguator {

	class BetweennessVertexScorer implements VertexScorer<Vertex, Double> {

		private final edu.uci.ics.jung.algorithms.scoring.BetweennessCentrality<Vertex, Edge> betweenness;
		private final int verticesCount;

		public BetweennessVertexScorer(Graph subgraph) {
			GraphJung<Graph> graphJung = Graphs.asGraphJung(graphType, subgraph);
			betweenness = new edu.uci.ics.jung.algorithms.scoring.BetweennessCentrality<Vertex, Edge>(graphJung);
			verticesCount = Graphs.numberOfVertices(subgraph);
		}

		@Override
		public Double getVertexScore(Vertex v) {
			double score = betweenness.getVertexScore(v);
			double normalizedScore = score / ((verticesCount - 1) * (verticesCount - 2));
			return normalizedScore;
		}

	}

	public static final BetweennessCentrality DIRECTED = new BetweennessCentrality(GraphType.DIRECTED_GRAPH);
	public static final BetweennessCentrality UNDIRECTED = new BetweennessCentrality(GraphType.UNDIRECTED_GRAPH);

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
	protected VertexScorer<Vertex, Double> getVertexScorer(Graph subgraph) {
		return new BetweennessVertexScorer(subgraph);
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " (graphType: " + graphType + " )";
	}

}
