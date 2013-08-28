package de.unima.dws.dbpediagraph.graphdb.disambiguate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.oupls.jung.GraphJung;

import de.unima.dws.dbpediagraph.graphdb.GraphUtil;
import edu.uci.ics.jung.algorithms.scoring.HITS;
import edu.uci.ics.jung.algorithms.scoring.HITS.Scores;

public class HITSCentrality implements LocalDisambiguator {

	@Override
	public List<WeightedUri> disambiguate(Collection<String> uris, Graph subgraph) {
		HITS<Vertex, Edge> hits = new HITS<Vertex, Edge>(new GraphJung<Graph>(subgraph));
		hits.evaluate();

		List<WeightedUri> wUris = new ArrayList<>();
		for (String uri : uris) {
			Scores scores = hits.getVertexScore(GraphUtil.getVertexByUri(subgraph, uri));
			wUris.add(new WeightedUri(uri, scores.authority));
		}
		return wUris;
	}

	@Override
	public LocalConnectivityMeasure getType() {
		return LocalConnectivityMeasure.HITS;
	}

}
