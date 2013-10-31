package de.unima.dws.dbpediagraph.graphdb.disambiguate.local;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.oupls.jung.GraphJung;

import de.unima.dws.dbpediagraph.graphdb.GraphType;
import de.unima.dws.dbpediagraph.graphdb.Graphs;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.AbstractLocalGraphDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.LocalGraphDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.model.ModelFactory;
import de.unima.dws.dbpediagraph.graphdb.model.Sense;
import de.unima.dws.dbpediagraph.graphdb.model.SurfaceForm;
import edu.uci.ics.jung.algorithms.scoring.VertexScorer;

/**
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

	public BetweennessCentrality(GraphType graphType, ModelFactory<T, U> factory) {
		super(graphType, factory);
	}

	@Override
	protected VertexScorer<Vertex, Double> getVertexScorer(Graph subgraph) {
		return new BetweennessVertexScorer(subgraph);
	}

}
