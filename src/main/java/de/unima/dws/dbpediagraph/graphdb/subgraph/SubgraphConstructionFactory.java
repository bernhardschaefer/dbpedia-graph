package de.unima.dws.dbpediagraph.graphdb.subgraph;

import com.tinkerpop.blueprints.Graph;

public final class SubgraphConstructionFactory {
	public static SubgraphConstruction newDefaultImplementation() {
		return new SubgraphConstructionNavigliNew(SubgraphConstructionSettings.getDefault());
	}

	public static SubgraphConstruction newDefaultImplementation(Graph graph, SubgraphConstructionSettings settings) {
		return new SubgraphConstructionNavigliNew(graph, settings);
	}

	// Suppress default constructor for noninstantiability
	private SubgraphConstructionFactory() {
		throw new AssertionError();
	}
}
