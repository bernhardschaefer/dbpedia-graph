package de.unima.dws.dbpediagraph.graphdb.disambiguate;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.Graphs;
import edu.uci.ics.jung.algorithms.scoring.VertexScorer;

public abstract class AbstractLocalGraphDisambiguator implements LocalGraphDisambiguator {

	@Override
	public List<SurfaceFormSenseScore> disambiguate(Collection<SurfaceFormSenses> surfaceFormsSenses, Graph subgraph) {
		VertexScorer<Vertex, Double> vertexScorer = getVertexScorer(subgraph);
		List<SurfaceFormSenseScore> senseScores = DisambiguatorHelper.initializeScores(surfaceFormsSenses);
		for (SurfaceFormSenseScore senseScore : senseScores) {
			double score = vertexScorer.getVertexScore(Graphs.vertexByUri(subgraph, senseScore.fullUri()));
			senseScore.setScore(score);
		}
		Collections.sort(senseScores);
		Collections.reverse(senseScores);
		return senseScores;
	}

	protected abstract VertexScorer<Vertex, Double> getVertexScorer(Graph subgraph);

}
