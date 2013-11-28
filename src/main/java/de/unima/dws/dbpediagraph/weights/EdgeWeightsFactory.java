package de.unima.dws.dbpediagraph.weights;

import java.util.*;

import org.apache.commons.configuration.Configuration;

import de.unima.dws.dbpediagraph.graph.GraphConfig;

/**
 * Noninstantiable graph weights factory class that provides the graph weights needed for disambiguation.
 * 
 * @author Bernhard Schäfer
 * 
 */
public final class EdgeWeightsFactory {

	private static final Map<EdgeWeightsType, EdgeWeights> edgeWeightsImpls;
	static {
		boolean readOnly = true, clear = false;
		Map<String, Integer> occCounts = OccurrenceCounts.loadPersistentOccCountsMap(GraphConfig.config(), clear,
				readOnly);

		edgeWeightsImpls = new EnumMap<>(EdgeWeightsType.class);
		edgeWeightsImpls.put(EdgeWeightsType.DUMMY, DummyEdgeWeights.INSTANCE);
		edgeWeightsImpls.put(EdgeWeightsType.COMB_IC, new CombinedInformationContent(occCounts));
		edgeWeightsImpls.put(EdgeWeightsType.JOINT_IC, new JointInformationContent(occCounts));
		edgeWeightsImpls.put(EdgeWeightsType.IC_PMI, new InfContentAndPointwiseMutuaInf(occCounts));
	}

	public static EdgeWeights dbpediaImplFromConfig(Configuration config) {
		EdgeWeightsType edgeWeightsType = EdgeWeightsType.fromConfig(config);
		return dbpediaWeightsfromEdgeWeightsType(edgeWeightsType);
	}

	public static EdgeWeights dbpediaWeightsfromEdgeWeightsType(EdgeWeightsType edgeWeightsType) {
		return edgeWeightsImpls.get(edgeWeightsType);
	}

	/**
	 * Enum where each type corresponds to one {@link EdgeWeights} implementation.
	 */
	enum EdgeWeightsType {
		DUMMY, JOINT_IC, COMB_IC, IC_PMI;

		private static final String CONFIG_EDGE_WEIGHTS_IMPL = "graph.edge.weights.impl";

		public static EdgeWeightsType fromConfig(Configuration config) {
			EdgeWeightsType edgeWeightType;
			String edgeWeightsImplName = config.getString(CONFIG_EDGE_WEIGHTS_IMPL);
			try {
				edgeWeightType = EdgeWeightsType.valueOf(edgeWeightsImplName);
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
						String.format(
								"Unknown edge weight type '%s' specified in config for key '%s'. Only the following are allowed: %s",
								edgeWeightsImplName, CONFIG_EDGE_WEIGHTS_IMPL,
								Arrays.toString(EdgeWeightsType.values())), e);
			}
			return edgeWeightType;
		}
	}

	// Suppress default constructor for non-instantiability
	private EdgeWeightsFactory() {
	}
}
