package de.unima.dws.dbpediagraph.graphdb.subgraph;

import java.lang.reflect.InvocationTargetException;

import com.tinkerpop.blueprints.Graph;

/**
 * Factory for retrieving a {@link SubgraphConstruction} implementation.
 * 
 * @author Bernhard Schäfer
 * 
 */
public final class SubgraphConstructionFactory {
	@SuppressWarnings("unused")
	private static final Class<? extends SubgraphConstruction> DEFAULT_SUBGRAPH_CONSTRUCTION = SubgraphConstructionIterative.class;

	public static SubgraphConstruction newSubgraphConstruction(Graph graph, SubgraphConstructionSettings settings) {
		//switch back to reflection based instantiation as soon as there are more than one implementations of SubgraphConstruction
		return new SubgraphConstructionIterative(graph, settings);
		// return newSubgraphConstruction(graph, settings, DEFAULT_SUBGRAPH_CONSTRUCTION);
	}

	@SuppressWarnings("unused")
	private static SubgraphConstruction newSubgraphConstruction(Graph graph, SubgraphConstructionSettings settings,
			Class<? extends SubgraphConstruction> subgraphConstructionClass) {
		SubgraphConstruction subgraphConstruction = null;
		try {
			subgraphConstruction = subgraphConstructionClass.getConstructor(Graph.class,
					SubgraphConstructionSettings.class).newInstance(graph, settings);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException("Error while trying to create instance of subgraph construction.", e);
		}
		return subgraphConstruction;
	}

	// Suppress default constructor for noninstantiability
	private SubgraphConstructionFactory() {
		throw new AssertionError();
	}
}
