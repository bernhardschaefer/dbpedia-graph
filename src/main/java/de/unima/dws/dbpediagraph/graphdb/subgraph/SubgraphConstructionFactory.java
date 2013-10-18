package de.unima.dws.dbpediagraph.graphdb.subgraph;

import java.lang.reflect.InvocationTargetException;

import com.tinkerpop.blueprints.Graph;

/**
 * Instance-controlled factory class for retrieving a {@link SubgraphConstruction} implementation.
 * 
 * @author Bernhard Schäfer
 * 
 */
public final class SubgraphConstructionFactory {
	private static final Class<? extends SubgraphConstruction> DEFAULT_SUBGRAPH_CONSTRUCTION = SubgraphConstructionRec.class;

	public static Class<? extends SubgraphConstruction> defaultClass() {
		return DEFAULT_SUBGRAPH_CONSTRUCTION;
	}

	public static SubgraphConstruction newDefaultImplementation(Graph graph, SubgraphConstructionSettings settings) {
		return newInstance(defaultClass(), settings, graph);
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
