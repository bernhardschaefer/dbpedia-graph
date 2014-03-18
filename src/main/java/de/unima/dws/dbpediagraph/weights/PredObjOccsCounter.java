package de.unima.dws.dbpediagraph.weights;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.*;

import de.unima.dws.dbpediagraph.graph.*;
import de.unima.dws.dbpediagraph.graph.GraphFactory;
import de.unima.dws.dbpediagraph.util.Counter;

/**
 * 
 * @author Bernhard Schäfer
 * 
 */
public class PredObjOccsCounter {
	private static final Logger logger = LoggerFactory.getLogger(PredObjOccsCounter.class);
	public static final String KEY_EDGE_COUNT = "EDGE_COUNT";

	public static void main(String[] args) throws FileNotFoundException {
		countAndPersistDBpediaGraphOccs();
	}

	public static void countAndPersistDBpediaGraphOccs() throws FileNotFoundException {
		logger.info("STARTING with counting and persisting DBpedia graph URI occurrences.");
		Graph graph = GraphFactory.getDBpediaGraph();

		Map<String, Integer> map = countGraphOccs(graph);
		graph.shutdown(); // free ram
		KryoMap.serializeMap(map, GraphConfig.config());
	}

	private static Map<String, Integer> countGraphOccs(Graph graph) {
		Counter c = new Counter("process edges", 1_000_000);

		Map<String, Integer> counts = new HashMap<>(1_000_000);
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

		return counts;
	}

	private static void incValueOfKey(Map<String, Integer> map, String key) {
		Integer count = map.get(key);
		if (count == null)
			count = 0;
		map.put(key, ++count);
	}

}
