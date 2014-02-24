package de.unima.dws.dbpediagraph.disambiguate.local;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graph.Graphs;
import edu.uci.ics.jung.algorithms.scoring.*;
import edu.uci.ics.jung.algorithms.scoring.HITS.Scores;

class HITSVertexScorer implements VertexScorer<Vertex, Double> {

	private final HITSWithPriors<Vertex, Edge> hits;

	// private final Map<Vertex, HitsScores> hitsScores;

	HITSVertexScorer(HITSWithPriors<Vertex, Edge> hits, int iterations) {
		this.hits = hits;

		hits.acceptDisconnectedGraph(true);
		hits.setMaxIterations(iterations);
		hits.evaluate();
		// hitsScores = calculateHitsScores(subgraph);
	}

	@Override
	public Double getVertexScore(Vertex v) {
		Scores scores = hits.getVertexScore(v);
		// HitsScores scores = hitsScores.get(v);
		// assign authority of 0 if v is not connected to any other vertex.
		double authority = Graphs.vertexHasNoNeighbours(v) ? 0 : scores.authority;
		return authority;
	}

}