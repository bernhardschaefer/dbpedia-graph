package de.unima.dws.dbpediagraph.demo;

import java.util.Map;

import com.tinkerpop.blueprints.*;

import de.unima.dws.dbpediagraph.graph.GraphFactory;
import de.unima.dws.dbpediagraph.graph.Graphs;
import de.unima.dws.dbpediagraph.weights.*;
import de.unima.dws.dbpediagraph.weights.EdgeWeightsFactory.EdgeWeightsType;

public class EdgeWeightsDemo {
	public static void main(String[] args) {
		Graph graph = GraphFactory.getDBpediaGraph();
		
		Map<String, Integer> occCounts = OccurrenceCounts.getDBpediaOccCounts();
		EdgeWeights jointIC = EdgeWeightsFactory.fromEdgeWeightsType(EdgeWeightsType.JOINT_IC, occCounts);
		EdgeWeights combIC = EdgeWeightsFactory.fromEdgeWeightsType(EdgeWeightsType.COMB_IC, occCounts);
		EdgeWeights icpmi = EdgeWeightsFactory.fromEdgeWeightsType(EdgeWeightsType.IC_PMI, occCounts);

		String fullUri = "http://dbpedia.org/resource/Michael_Jordan";
		Vertex v = Graphs.vertexByFullUri(graph, fullUri);

		for (Edge e : v.getEdges(Direction.OUT)) {
			// assure TRACE level weights package logging
			jointIC.transform(e);
			combIC.transform(e);
			icpmi.transform(e);
		}

		graph.shutdown();
	}
}
