package de.unima.dws.dbpediagraph.demo;

import com.tinkerpop.blueprints.Graph;

import de.unima.dws.dbpediagraph.graph.GraphFactory;
import de.unima.dws.dbpediagraph.graph.Graphs;

/**
 * Demo class for retrieving statistics of the currently configured DBpedia graph.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class GraphStats {

	public static void main(String[] args) {
		Graph graph = GraphFactory.getDBpediaGraph();
		printGraphStats(graph);
		graph.shutdown();
	}

	private static void printGraphStats(Graph graph) {
		System.out.printf("Vertices: %,d ", Graphs.verticesCount(graph));
		System.out.printf("Edges: %,d %n", Graphs.edgesCount(graph));
	}

}
