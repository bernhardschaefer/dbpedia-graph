package de.unima.dws.dbpediagraph.graphdb.subgraph;

import com.tinkerpop.blueprints.Graph;

public final class SubgraphConstructionFactory {
	public static SubgraphConstruction newDefaultImplementation() {
		return new SubgraphConstructionNavigliNew();
	}

	public static SubgraphConstruction newDefaultImplementation(Graph graph) {
		return new SubgraphConstructionNavigliNew(graph);
	}

	// Suppress default constructor for noninstantiability
	private SubgraphConstructionFactory() {
		throw new AssertionError();
	}
}
