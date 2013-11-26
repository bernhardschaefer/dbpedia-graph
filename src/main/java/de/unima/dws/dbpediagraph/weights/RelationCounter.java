package de.unima.dws.dbpediagraph.weights;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.io.File;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.*;

import de.unima.dws.dbpediagraph.graph.*;
import de.unima.dws.dbpediagraph.graph.GraphFactory;
import de.unima.dws.dbpediagraph.util.BerkeleyDB;
import de.unima.dws.dbpediagraph.util.PersistedMap;

public class RelationCounter {
	private static final Logger logger = LoggerFactory.getLogger(RelationCounter.class);
	private static final long TICK_RATE = 100_000;

	public static void main(String[] args) {
		// String dbName = args[args.length -1];
		String dbName = "all";

		String location = GraphConfig.config().getString("graph.weights.directory");

		RelationCounter c = new RelationCounter();

		Graph graph = GraphFactory.getDBpediaGraph();

		c.countThemAll(graph, location, dbName);
		graph.shutdown();
	}

	private void countThemAll(Graph graph, String location, String dbName) {
		Map<String, Integer> map = newMap();
		doIt(graph, map, map, map);

		PersistedMap<String, Integer> db = new BerkeleyDB.Builder<>(new File(location), dbName, String.class,
				Integer.class).build();

		dumpToDB(map, db);
		db.close();
	}

	private void dumpToDB(Map<String, Integer> map, PersistedMap<String, Integer> db) {
		//TODO dump sums
		for (Map.Entry<String, Integer> entry : map.entrySet())
			db.put(entry.getKey(), entry.getValue());
	}

	private void doIt(Graph graph, Map<String, Integer> predCounts, Map<String, Integer> objCounts,
			Map<String, Integer> predObjCounts) {
		long tickTime = System.currentTimeMillis();
		long processedEdges = 0;
		for (Edge edge : graph.getEdges()) {
			String shortPredUri = edge.getLabel();

			Vertex in = edge.getVertex(Direction.IN);
			if (in == null) {
				logger.warn("edge with label {} has no in vertex.", shortPredUri);
				continue;
			}
			String shortObjUri = Graphs.uriOfVertex(in);

			incValueOfKey(predCounts, shortPredUri);
			incValueOfKey(objCounts, shortObjUri);
			incValueOfKey(predObjCounts, shortPredUri + shortObjUri);

			if ((++processedEdges % TICK_RATE) == 0) {
				long now = System.currentTimeMillis();
				long tickTimeDelta = now - tickTime;
				tickTime = now;
				logger.info(String.format("total processed edges: %,d @ ~%.2f sec/%,d triples.", processedEdges,
						tickTimeDelta / 1000.0, TICK_RATE));
			}
		}

	}

	private void incValueOfKey(Map<String, Integer> map, String key) {
		Integer count = map.get(key);
		if (count == null)
			count = 0;
		map.put(key, ++count);
	}

	public static Map<String, Integer> newMap() {
		return new Object2IntOpenHashMap<String>();
	}

}
