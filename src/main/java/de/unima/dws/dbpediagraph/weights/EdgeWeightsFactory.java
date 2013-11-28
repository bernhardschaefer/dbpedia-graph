package de.unima.dws.dbpediagraph.weights;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.io.File;
import java.util.*;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unima.dws.dbpediagraph.graph.GraphConfig;
import de.unima.dws.dbpediagraph.util.BerkeleyDB;
import de.unima.dws.dbpediagraph.util.PersistentMap;

/**
 * Noninstantiable graph weights factory class that provides the graph weights needed for disambiguation.
 * 
 * @author Bernhard Schäfer
 * 
 */
public final class EdgeWeightsFactory {
	private static final Logger logger = LoggerFactory.getLogger(EdgeWeightsFactory.class);

	private static final Map<String, Integer> occCounts;
	private static final Map<EdgeWeightsType, EdgeWeights> edgeWeightsImpls;
	static {
		boolean readOnly = true, clear = false;
		occCounts = loadPersistentOccCountsMap(GraphConfig.config(), clear, readOnly);

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

	static Map<String, Integer> newTransientMap() {
		return new Object2IntOpenHashMap<String>();
	}

	static PersistentMap<String, Integer> newPersistentWeightsMap() {
		boolean readOnly = false, clear = true;
		return loadPersistentOccCountsMap(GraphConfig.config(), clear, readOnly);
	}

	static PersistentMap<String, Integer> loadPersistentOccCountsMap(Configuration config, boolean clear,
			boolean readOnly) {
		long startTime = System.currentTimeMillis();

		String dbName = "all";
		String location = config.getString("graph.occ.counts.directory");
		BerkeleyDB<String, Integer> db = new BerkeleyDB.Builder<>(new File(location), dbName, String.class,
				Integer.class).readOnly(readOnly).build();
		if (clear)
			db.clear();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				if (occCounts instanceof PersistentMap) {
					logger.info("Shutting down occurrence counts");
					((PersistentMap<String, Integer>) occCounts).close();
				}
			}
		});

		logger.info("Graph weights loading time {} sec", (System.currentTimeMillis() - startTime) / 1000.0);
		return db;
	}

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

	public static void main(String[] args) {
		boolean clear = false, readOnly = true;
		PersistentMap<String, Integer> persistentMap = loadPersistentOccCountsMap(GraphConfig.config(), clear, readOnly);
		if (!(persistentMap instanceof BerkeleyDB))
			throw new IllegalStateException("The loaded map is no berkeley db.");
		BerkeleyDB<String, Integer> db = (BerkeleyDB<String, Integer>) persistentMap;
		BerkeleyDB.queryContent(db);
		db.close();
	}

	// Suppress default constructor for noninstantiability
	private EdgeWeightsFactory() {
		throw new AssertionError();
	}
}
