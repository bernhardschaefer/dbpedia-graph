package de.unima.dws.dbpediagraph.weights;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.*;

import de.unima.dws.dbpediagraph.graph.*;
import de.unima.dws.dbpediagraph.graph.GraphFactory;
import de.unima.dws.dbpediagraph.util.Counter;
import de.unima.dws.dbpediagraph.util.PersistentMap;

/**
 * 
 * @author Bernhard Schäfer
 * 
 */
public class PredObjOccsCounter {
	private static final Logger logger = LoggerFactory.getLogger(PredObjOccsCounter.class);
	public static final String KEY_EDGE_COUNT = "EDGE_COUNT";

	public static void main(String[] args) {
		double totalMemoryGb = Runtime.getRuntime().maxMemory() / (1024.0 * 1024.0 * 1024.0);
		if (totalMemoryGb > 2)
			countDBpediaGraphOccsIntoDB();
		else
			countDBpediaGraphOccsIntoDBSlow();
	}

	public static void countDBpediaGraphOccsIntoDB() {
		Graph graph = GraphFactory.getDBpediaGraph();

		Map<String, Integer> map = GraphWeightsFactory.newTransientMap();
		countGraphOccsIntoMap(graph, map);

		PersistentMap<String, Integer> db = newPersistenWeightsMap();
		dumpMapToPersistentMap(map, db);
		db.close();

		graph.shutdown();
	}

	private static PersistentMap<String, Integer> newPersistenWeightsMap() {
		boolean readOnly = false, clear = true;
		return GraphWeightsFactory.loadPersistentWeightsMap(GraphConfig.config(), clear, readOnly);
	}

	public static void countDBpediaGraphOccsIntoDBSlow() {
		Graph graph = GraphFactory.getDBpediaGraph();

		PersistentMap<String, Integer> db = newPersistenWeightsMap();
		countGraphOccsIntoMap(graph, db);
		db.close();

		graph.shutdown();
	}

	private static void dumpMapToPersistentMap(Map<String, Integer> map, PersistentMap<String, Integer> db) {
		Counter c = new Counter("dump map entries", 1_000_000);
		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			db.put(entry.getKey(), entry.getValue());
			c.inc();
		}
		c.finish();
	}

	private static void countGraphOccsIntoMap(Graph graph, Map<String, Integer> counts) {
		Counter c = new Counter("process edges", 1_000_000);

		for (Edge edge : graph.getEdges()) {
			String shortPredUri = edge.getLabel();

			Vertex in = edge.getVertex(Direction.IN);
			if (in == null) {
				logger.warn("edge with label {} has no in vertex.", shortPredUri);
				continue;
			}
			String shortObjUri = Graphs.uriOfVertex(in);

			incValueOfKey(counts, shortPredUri);
			incValueOfKey(counts, shortObjUri);
			incValueOfKey(counts, shortPredUri + shortObjUri);

			c.inc();
		}

		counts.put(KEY_EDGE_COUNT, c.count());
		c.finish();
	}

	private static void incValueOfKey(Map<String, Integer> map, String key) {
		Integer count = map.get(key);
		if (count == null)
			count = 0;
		map.put(key, ++count);
	}

}
