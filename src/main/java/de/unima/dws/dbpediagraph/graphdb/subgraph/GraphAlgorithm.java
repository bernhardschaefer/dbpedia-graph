package de.unima.dws.dbpediagraph.graphdb.subgraph;

import com.tinkerpop.blueprints.Graph;

import de.unima.dws.dbpediagraph.graphdb.GraphProvider;

public abstract class GraphAlgorithm {
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
