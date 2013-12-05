package de.unima.dws.dbpediagraph.weights;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.io.File;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unima.dws.dbpediagraph.graph.GraphConfig;
import de.unima.dws.dbpediagraph.util.*;

public class OccurrenceCounts {
	private static final Logger logger = LoggerFactory.getLogger(OccurrenceCounts.class);

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
		long startTime = System.nanoTime();

		String dbName = "all";
		String location = config.getString("graph.occ.counts.directory");
		final PersistentMap<String, Integer> db = new BerkeleyDB.Builder<>(new File(location), dbName, String.class,
				Integer.class).readOnly(readOnly).build();
		if (clear)
			db.clear();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				if (db instanceof PersistentMap) {
					logger.info("Shutting down occurrence counts");
					db.close();
				}
			}
		});

		logger.info("Graph weights loading time {} sec", Counter.elapsedSecs(startTime));
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

}
