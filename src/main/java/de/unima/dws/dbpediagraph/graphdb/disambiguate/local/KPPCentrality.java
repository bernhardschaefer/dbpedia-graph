package de.unima.dws.dbpediagraph.graphdb.disambiguate.local;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.GraphUtil;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.LocalConnectivityMeasure;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.LocalDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.WeightedUri;
import de.unima.dws.dbpediagraph.graphdb.wrapper.GraphJungUndirected;
import edu.uci.ics.jung.algorithms.scoring.DistanceCentralityScorer;
import edu.uci.ics.jung.algorithms.shortestpath.Distance;
import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;
import edu.uci.ics.jung.graph.Hypergraph;

public class KPPCentrality implements LocalDisambiguator {
	static class KPPScorer extends DistanceCentralityScorer<Vertex, Edge> {
		public KPPScorer(Hypergraph<Vertex, Edge> graph) {
			super(graph, false);
		}
	}

	@Override
	public List<WeightedUri> disambiguate(Collection<String> uris, Graph subgraph) {
		// ClosenessCentrality<Vertex, Edge>
		Distance<Vertex> sp = new UnweightedShortestPath<>(new GraphJungUndirected(subgraph));
		int numberOfVertices = GraphUtil.getNumberOfVertices(subgraph);

		// KPPScorer kpp = new KPPScorer(new GraphJung<Graph>(subgraph));
		List<WeightedUri> weightedUris = new LinkedList<>();
		for (String uri : uris) {
			Vertex v = GraphUtil.getVertexByUri(subgraph, uri);
			// double score = kpp.getVertexScore(v);
			Map<Vertex, Number> distances = sp.getDistanceMap(v);
			double sumInverseShortestDistances = 0;
			for (Vertex otherVertex : subgraph.getVertices()) {
				if (otherVertex.equals(v))
					continue;
				Number distance = distances.get(otherVertex);
				double inverseShortestDistance;
				if (distance == null || distance.doubleValue() == 0)
					// if there is no path, the distance is the constant 1/(numberOfVertices-1)
					inverseShortestDistance = 1.0 / (numberOfVertices - 1);
				else
					inverseShortestDistance = 1.0 / distance.doubleValue();
				sumInverseShortestDistances += inverseShortestDistance;
			}

			double score = sumInverseShortestDistances / (numberOfVertices - 1);
			weightedUris.add(new WeightedUri(uri, score));
		}

		Collections.sort(weightedUris);
		Collections.reverse(weightedUris);

		return weightedUris;
	}

	@Override
	public LocalConnectivityMeasure getType() {
		return LocalConnectivityMeasure.KPP;
	}

}
