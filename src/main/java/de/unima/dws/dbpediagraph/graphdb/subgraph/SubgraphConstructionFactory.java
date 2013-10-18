package de.unima.dws.dbpediagraph.graphdb.subgraph;

import java.lang.reflect.InvocationTargetException;

import com.tinkerpop.blueprints.Graph;

public final class SubgraphConstructionFactory {
	public static Class<? extends SubgraphConstruction> defaultClass() {
		return SubgraphConstructionRec.class;
	}

	public static SubgraphConstruction newDefaultImplementation() {
		return newDefaultImplementation(null, SubgraphConstructionSettings.getDefault());
	}

	public static SubgraphConstruction newDefaultImplementation(Graph graph, SubgraphConstructionSettings settings) {
		return new SubgraphConstructionRec(graph, settings);
	}

	public static SubgraphConstruction newInstance(Class<? extends SubgraphConstruction> subgraphConstructionClass,
			SubgraphConstructionSettings settings, Graph graph) {
		SubgraphConstruction subgraphConstruction = null;
		try {
			subgraphConstruction = subgraphConstructionClass.getConstructor(Graph.class,
					SubgraphConstructionSettings.class).newInstance(graph, settings);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException("Error while trying to construct test graph.", e);
		}
		return subgraphConstruction;
	}

	// Suppress default constructor for noninstantiability
	private SubgraphConstructionFactory() {
		throw new AssertionError();
	}
}
