package de.unima.dws.dbpediagraph.graph;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.io.File;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unima.dws.dbpediagraph.util.BerkeleyDB;
import de.unima.dws.dbpediagraph.util.PersistentMap;
import de.unima.dws.dbpediagraph.weights.GraphWeights;
import de.unima.dws.dbpediagraph.weights.MapGraphWeights;

/**
 * Noninstantiable graph weights factory class that provides the graph weights needed for disambiguation.
 * 
 * @author Bernhard Schäfer
 * 
 */
public final class GraphWeightsFactory {
	private static final Logger logger = LoggerFactory.getLogger(GraphWeightsFactory.class);

	private static class DBpediaGraphWeightsHolder {
		public static final GraphWeights GRAPH_WEIGHTS = openGraphWeights();

		private static GraphWeights openGraphWeights() {
			boolean readOnly = true, clear = false;
			PersistentMap<String, Integer> persistentMap = loadPersistentWeightsMap(GraphConfig.config(), clear, readOnly);
			return new MapGraphWeights(persistentMap);
		}
	}

	public static GraphWeights getDBpediaGraphWeights() {
		return DBpediaGraphWeightsHolder.GRAPH_WEIGHTS;
	}

	public static Map<String, Integer> newTransientMap() {
		return new Object2IntOpenHashMap<String>();
	}

	public static PersistentMap<String, Integer> loadPersistentWeightsMap(Configuration config, boolean clear,
			boolean readOnly) {
		long startTime = System.currentTimeMillis();
		
		String dbName = "all";
		String location = config.getString("graph.weights.directory");
		BerkeleyDB<String, Integer> db = new BerkeleyDB.Builder<>(new File(location), dbName, String.class,
				Integer.class).readOnly(readOnly).build();
		if (clear)
			db.clear();
		
		logger.info("Graph weights loading time {} sec", (System.currentTimeMillis() - startTime) / 1000.0);
		return db;
	}

	// Suppress default constructor for noninstantiability
	private GraphWeightsFactory() {
		throw new AssertionError();
	}
}
