package de.unima.dws.dbpediagraph.graphdb.algorithms;

import com.tinkerpop.blueprints.Graph;

import de.unima.dws.dbpediagraph.graphdb.GraphProvider;

public class GraphAlgorithm {
	protected final Graph graph;

	public GraphAlgorithm() {
		this.graph = GraphProvider.getInstance().getGraph();
	}

	public Graph getGraph() {
		return graph;
	}

	public void shutdown() {
		graph.shutdown();
	}

}
