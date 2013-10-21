package de.unima.dws.dbpediagraph.graphdb.disambiguate.local;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.oupls.jung.GraphJung;

import de.unima.dws.dbpediagraph.graphdb.Graphs;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.LocalDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.WeightedSense;
import edu.uci.ics.jung.algorithms.scoring.HITS;
import edu.uci.ics.jung.algorithms.scoring.HITS.Scores;

public class HITSCentrality implements LocalDisambiguator {

	@Override
	public List<WeightedSense> disambiguate(Collection<String> senses, Graph subgraph) {
		HITS<Vertex, Edge> hits = new HITS<Vertex, Edge>(new GraphJung<Graph>(subgraph));
		hits.evaluate();

		List<WeightedSense> wUris = new ArrayList<>();
		for (String uri : senses) {
			Scores scores = hits.getVertexScore(Graphs.vertexByUri(subgraph, uri));
			wUris.add(new WeightedSense(uri, scores.authority));
		}
		return wUris;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}
