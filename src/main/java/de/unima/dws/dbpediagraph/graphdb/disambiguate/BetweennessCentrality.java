package de.unima.dws.dbpediagraph.graphdb.disambiguate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.oupls.jung.GraphJung;

import de.unima.dws.dbpediagraph.graphdb.GraphUtil;

public class BetweennessCentrality implements LocalDisambiguator {

	@Override
	public List<WeightedUri> disambiguate(Collection<String> uris, Graph subgraph) {
		edu.uci.ics.jung.algorithms.scoring.BetweennessCentrality<Vertex, Edge> hits = new edu.uci.ics.jung.algorithms.scoring.BetweennessCentrality<Vertex, Edge>(
				new GraphJung<Graph>(subgraph));

		List<WeightedUri> wUris = new ArrayList<>();
		for (String uri : uris) {
			double weight = hits.getVertexScore(GraphUtil.getVertexByUri(subgraph, uri));
			wUris.add(new WeightedUri(uri, weight));
		}
		return wUris;
	}

	@Override
	public LocalConnectivityMeasure getType() {
		return LocalConnectivityMeasure.Betweenness;
	}

}
