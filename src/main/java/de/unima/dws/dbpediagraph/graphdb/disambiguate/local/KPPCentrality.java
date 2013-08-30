package de.unima.dws.dbpediagraph.graphdb.disambiguate.local;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.GraphUtil;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.ConnectivityMeasure;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.LocalDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.WeightedSense;
import de.unima.dws.dbpediagraph.graphdb.wrapper.GraphJungUndirected;
import edu.uci.ics.jung.algorithms.shortestpath.Distance;
import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;

public class KPPCentrality implements LocalDisambiguator {

	@Override
	public List<WeightedSense> disambiguate(Collection<String> senses, Graph subgraph) {
		Distance<Vertex> distances = new UnweightedShortestPath<>(new GraphJungUndirected(subgraph));
		int numberOfVertices = GraphUtil.getNumberOfVertices(subgraph);

		List<WeightedSense> weightedUris = new LinkedList<>();
		for (String sense : senses) {
			Vertex v = GraphUtil.getVertexByUri(subgraph, sense);
			Map<Vertex, Number> distancesFromVertex = distances.getDistanceMap(v);
			double sumInverseShortestDistances = 0;
			for (Vertex otherVertex : subgraph.getVertices()) {
				if (otherVertex.equals(v))
					continue;
				Number distance = distancesFromVertex.get(otherVertex);
				double inverseShortestDistance;
				if (distance == null || distance.doubleValue() == 0)
					// if there is no path, the distance is the constant 1/(numberOfVertices-1)
					inverseShortestDistance = 1.0 / (numberOfVertices - 1);
				else
					inverseShortestDistance = 1.0 / distance.doubleValue();
				sumInverseShortestDistances += inverseShortestDistance;
			}

			double score = sumInverseShortestDistances / (numberOfVertices - 1);
			weightedUris.add(new WeightedSense(sense, score));
		}

		Collections.sort(weightedUris);
		Collections.reverse(weightedUris);

		return weightedUris;
	}

	@Override
	public ConnectivityMeasure getType() {
		return ConnectivityMeasure.KPP;
	}

}
