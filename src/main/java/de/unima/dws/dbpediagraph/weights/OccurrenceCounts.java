package de.unima.dws.dbpediagraph.weights;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;

import de.unima.dws.dbpediagraph.graph.GraphConfig;
import de.unima.dws.dbpediagraph.util.BerkeleyDB;
import de.unima.dws.dbpediagraph.util.PersistentMap;

public class OccurrenceCounts {
	private static final Logger logger = LoggerFactory.getLogger(OccurrenceCounts.class);

	static final String CONFIG_EDGE_WARMUP = "graph.edge.weights.warmup";

	/**
	 * Inner class for lazy-loading DBpedia occurrence counts so that other counts can be used for testing etc.
	 */
	private static class DBpediaOccCountsHolder {
		private static final Map<String, Integer> OCC_COUNTS;
		static {
			boolean readOnly = true, clear = false;
			OCC_COUNTS = OccurrenceCounts.loadPersistentOccCountsMap(GraphConfig.config(), clear, readOnly);
		}
	}

	public static Map<String, Integer> getDBpediaOccCounts() {
		return DBpediaOccCountsHolder.OCC_COUNTS;
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
		Stopwatch stopwatch = Stopwatch.createStarted();

		String dbName = "all";
		String location = config.getString("graph.occ.counts.directory");
		final PersistentMap<String, Integer> db = new BerkeleyDB.Builder<>(new File(location), dbName, String.class,
				Integer.class).readOnly(readOnly).build();
		if (clear) {
			db.clear();
			logger.info("Clearing existing DBpedia URI count map at {}", location);
		}

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				if (db instanceof PersistentMap) {
					logger.info("Shutting down occurrence counts");
					db.close();
				}
			}
		});

		logger.info("Graph weights loading time {}", stopwatch);
		return db;
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

	// Suppress default constructor for non-instantiability
	private OccurrenceCounts() {
		throw new AssertionError();
	}

	public static void doWarmup(Map<String, Integer> occCounts) {
		logger.info("Starting edge weights warmup");
		Stopwatch stopwatch = Stopwatch.createStarted();
		int counter = 0;
		long weightSum = 0;
		for (Entry<String, Integer> entry : occCounts.entrySet()) {
			counter++;
			weightSum += entry.getValue();
		}
		logger.info("Traversed {} edge weights in {} (total weight sum: {}).", counter, stopwatch, weightSum);
	}

	public static void doWarmupIfConfigured(Configuration config) {
		if (config.getBoolean(OccurrenceCounts.CONFIG_EDGE_WARMUP))
			doWarmup(getDBpediaOccCounts());
	}

}
