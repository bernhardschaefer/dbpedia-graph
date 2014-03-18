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
 * Main functionality in {@link #countAndPersistDBpediaGraphOccs(Graph)}.
 * @author Bernhard Schäfer
 * 
 */
public class PredObjOccsCounter {
	private static final Logger logger = LoggerFactory.getLogger(PredObjOccsCounter.class);
	public static final String KEY_EDGE_COUNT = "EDGE_COUNT";

	public static void main(String[] args) throws FileNotFoundException {
		Graph graph = GraphFactory.getDBpediaGraph();
		countAndPersistDBpediaGraphOccs(graph);
		graph.shutdown();
	}

	/**
	 * Counts the occurrences of predicates , objects and predicate-object combinations within the provided graph.
	 * Divided by the number of edges these counts represent P(pred), P(obj), and P(pred,obj).
	 */
	public static void countAndPersistDBpediaGraphOccs(Graph graph) throws FileNotFoundException {
		logger.info("STARTING with counting and persisting DBpedia graph URI occurrences.");
		Map<String, Integer> map = countGraphOccs(graph);
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
