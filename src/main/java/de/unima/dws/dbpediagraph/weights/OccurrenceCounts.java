package de.unima.dws.dbpediagraph.weights;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.google.common.base.Stopwatch;

import de.unima.dws.dbpediagraph.graph.GraphConfig;

public class OccurrenceCounts {
	private static final Logger logger = LoggerFactory.getLogger(OccurrenceCounts.class);

	static final String CONFIG_EDGE_WARMUP = "graph.edge.weights.warmup";
	static final String CONFIG_EDGE_COUNTS_FILE = "graph.occ.counts.file";

	/**
	 * Inner class for lazy-loading DBpedia occurrence counts so that other counts can be used for testing etc.
	 */
	private static class DBpediaOccCountsHolder {
		private static final Map<String, Integer> OCC_COUNTS;
		static {
			OCC_COUNTS = OccurrenceCounts.loadOccCountsMap(GraphConfig.config());
		}
	}

	public static Map<String, Integer> getDBpediaOccCounts() {
		return DBpediaOccCountsHolder.OCC_COUNTS;
	}

	public static Map<String, Integer> loadOccCountsMap(Configuration config) {
		logger.info("Start loading edge weights");
		Stopwatch stopwatch = Stopwatch.createStarted();
		String fileName = config.getString(CONFIG_EDGE_COUNTS_FILE);
		Kryo kryo = KryoMap.getDefault();

		Input input;
		try {
			input = new Input(new FileInputStream(fileName));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		@SuppressWarnings("unchecked")
		Map<String, Integer> map = kryo.readObject(input, HashMap.class);
		input.close();

		logger.info("Edge weights loading time {}", stopwatch);

		return map;
	}

	public static void main(String[] args) {
		Map<String, Integer> map = loadOccCountsMap(GraphConfig.config());
		queryContent(map);
	}

	public static <V> void queryContent(Map<String, Integer> map) {
		String line = "";

		while (true) {
			System.out.println("Please enter a key, then press <return> (type \"exit\" to quit)");
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

			try {
				line = input.readLine();
				if (line.startsWith("exit")) {
					System.out.println("QUITTING, thank you ... ");
					break;
				}
				System.out.println("MAP VALUE: " + line + " => " + map.get(line));

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// Suppress default constructor for non-instantiability
	private OccurrenceCounts() {
		throw new AssertionError();
	}
}
