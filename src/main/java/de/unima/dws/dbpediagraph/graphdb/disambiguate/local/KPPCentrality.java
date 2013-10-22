package de.unima.dws.dbpediagraph.graphdb.disambiguate.local;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.oupls.jung.GraphJung;

import de.unima.dws.dbpediagraph.graphdb.GraphType;
import de.unima.dws.dbpediagraph.graphdb.Graphs;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.LocalDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.WeightedSense;
import edu.uci.ics.jung.algorithms.shortestpath.Distance;
import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;

/**
 * @author Bernhard Schäfer
 */
public enum KPPCentrality implements LocalDisambiguator {
	DIRECTED(GraphType.DIRECTED_GRAPH), UNDIRECTED(GraphType.UNDIRECTED_GRAPH);

	public static KPPCentrality forGraphType(GraphType graphType) {
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

	private KPPCentrality(GraphType graphType) {
		this.graphType = graphType;
	}

	@Override
	public List<WeightedSense> disambiguate(Collection<String> senses, Graph subgraph) {
		GraphJung<Graph> graphJung = Graphs.asGraphJung(graphType, subgraph);
		Distance<Vertex> distances = new UnweightedShortestPath<>(graphJung);
		int numberOfVertices = Graphs.numberOfVertices(subgraph);

		List<WeightedSense> weightedUris = new LinkedList<>();
		for (String sense : senses) {
			Vertex v = Graphs.vertexByUri(subgraph, sense);
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
	public String toString() {
		return this.getClass().getSimpleName() + " (graphType: " + graphType + " )";
	}

}
