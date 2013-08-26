package de.unima.dws.dbpediagraph.graphdb.subgraph;

import java.util.Collection;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.GraphProvider;
import de.unima.dws.dbpediagraph.graphdb.GraphUtil;
import de.unima.dws.dbpediagraph.graphdb.util.GraphPrinter;

public class TestSubgraphConstruction {
	public static void main(String[] args) {
		Graph graph = GraphProvider.getInstance().getGraph();

		// SubgraphConstruction sc = new SubgraphConstructionNaive(graph);
		SubgraphConstruction sc = new SubgraphConstructionNavigli(graph);
		Collection<Vertex> vertices = GraphUtil.getTestVertices(graph);
		Graph subGraph = sc.createSubgraph(vertices);
		GraphPrinter.printGraphStatistics(subGraph);

		graph.shutdown();
	}
}
