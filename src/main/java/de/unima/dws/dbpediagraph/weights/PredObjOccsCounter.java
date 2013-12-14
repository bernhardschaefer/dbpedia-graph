package de.unima.dws.dbpediagraph.weights;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.*;

import de.unima.dws.dbpediagraph.graph.GraphFactory;
import de.unima.dws.dbpediagraph.graph.Graphs;
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
		countAndPersistDBpediaGraphOccs();
	}

	public static void countAndPersistDBpediaGraphOccs() {
		logger.info("STARTING with counting and persisting DBpedia graph URI occurrences.");
		Graph graph = GraphFactory.getDBpediaGraph();
		PersistentMap<String, Integer> db = OccurrenceCounts.newPersistentWeightsMap();

		double minGb = 2;
		if (runtimeHasEnoughMemory(minGb)) {
			logger.info("Using fast counter since more than {} GB Ram available.", minGb);
			Map<String, Integer> map = OccurrenceCounts.newTransientMap();
			countGraphOccsIntoMap(graph, map);
			graph.shutdown(); // free ram
			dumpMapToPersistentMap(map, db);
			db.close();
		} else {
			logger.info("Using slow counter since less than {} GB Ram available.", minGb);
			countGraphOccsIntoMap(graph, db);
			graph.shutdown();
			db.close();
		}
	}

	private static boolean runtimeHasEnoughMemory(double minGb) {
		double totalMemoryGb = Runtime.getRuntime().maxMemory() / (1024.0 * 1024.0 * 1024.0);
		return totalMemoryGb >= minGb;
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
			String shortPredUri = Graphs.shortUriOfEdge(edge);
			String shortObjUri = Graphs.shortUriOfVertex(edge.getVertex(Direction.IN));

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
