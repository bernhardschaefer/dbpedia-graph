package de.unima.dws.dbpediagraph.weights;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.*;

import org.apache.commons.configuration.Configuration;

/**
 * Noninstantiable graph weights factory class that provides the graph weights needed for disambiguation.
 * 
 * @author Bernhard Schäfer
 * 
 */
public final class EdgeWeightsFactory {

	/**
	 * Inner class for lazy-loading DBpedia edge weights so that other edge weights could be used in future.
	 */
	private static class DBpediaEdgeWeights {
		private static final Map<EdgeWeightsType, EdgeWeights> DBPEDIA_EDGE_WEIGHTS;
		static {
			Map<String, Integer> occCounts = OccurrenceCounts.getDBpediaOccCounts();
			DBPEDIA_EDGE_WEIGHTS = new EnumMap<>(EdgeWeightsType.class);
			DBPEDIA_EDGE_WEIGHTS.put(EdgeWeightsType.DUMMY, DummyEdgeWeights.INSTANCE);
			DBPEDIA_EDGE_WEIGHTS.put(EdgeWeightsType.COMB_IC, new CombinedInformationContent(occCounts));
			DBPEDIA_EDGE_WEIGHTS.put(EdgeWeightsType.JOINT_IC, new JointInformationContent(occCounts));
			DBPEDIA_EDGE_WEIGHTS.put(EdgeWeightsType.IC_PMI, new InfContentAndPointwiseMutuaInf(occCounts));
		}
	}

	public static EdgeWeights fromConfig(Configuration config, Map<String, Integer> occCounts) {
		EdgeWeightsType edgeWeightsType = EdgeWeightsType.fromConfig(config);
		return fromEdgeWeightsType(edgeWeightsType, occCounts);
	}

	public static EdgeWeights fromEdgeWeightsType(EdgeWeightsType edgeWeightsType, Map<String, Integer> occCounts) {
		switch (edgeWeightsType) {
		case COMB_IC:
			return new CombinedInformationContent(occCounts);
		case IC_PMI:
			return new InfContentAndPointwiseMutuaInf(occCounts);
		case JOINT_IC:
			return new JointInformationContent(occCounts);
		case DUMMY:
			return DummyEdgeWeights.INSTANCE;
		}
		throw new IllegalArgumentException("The specified edge weights type is not valid: " + edgeWeightsType);
	}

	public static EdgeWeights dbpediaFromConfig(Configuration config) {
		EdgeWeightsType edgeWeightsType = EdgeWeightsType.fromConfig(config);
		return dbpediaFromEdgeWeightsType(edgeWeightsType);
	}

	public static EdgeWeights dbpediaFromEdgeWeightsType(EdgeWeightsType edgeWeightsType) {
		// prevent loading of DBpedia edge weights when DUMMY is requested
		if (checkNotNull(edgeWeightsType) == EdgeWeightsType.DUMMY)
			return DummyEdgeWeights.INSTANCE;
		else
			return DBpediaEdgeWeights.DBPEDIA_EDGE_WEIGHTS.get(edgeWeightsType);
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
