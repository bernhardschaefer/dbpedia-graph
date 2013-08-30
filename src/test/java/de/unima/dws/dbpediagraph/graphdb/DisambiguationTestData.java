package de.unima.dws.dbpediagraph.graphdb;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Graph;

import de.unima.dws.dbpediagraph.graphdb.disambiguate.LocalConnectivityMeasure;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.LocalDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.WeightedSense;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstruction;
import de.unima.dws.dbpediagraph.graphdb.util.FileUtils;

public class DisambiguationTestData extends SubgraphTestData {
	private static final Logger logger = LoggerFactory.getLogger(DisambiguationTestData.class);
	private static final double DELTA = 0.005;
	/** Local Connectivity Measure Results for the example from Navigli&Lapata (2010) */
	private static final String NL_LOCAL_RESULTS = "/nl-local-test.results";

	private final Graph subgraph;
	private final List<WeightedSense> weightedUris;
	private Map<String, Map<LocalConnectivityMeasure, Double>> localMeasureResults;
	private final LocalDisambiguator disambiguator;

	public DisambiguationTestData(LocalDisambiguator disambiguator, SubgraphConstruction subgraphConstruction) {
		super();
		try {
			setUpDisambiguationResults();
		} catch (IOException | URISyntaxException e) {
			throw new RuntimeException("Error while trying to construct test results.", e);
		}

		subgraphConstruction.setGraph(graph);
		subgraph = subgraphConstruction.createSubgraph(senses);
		this.disambiguator = disambiguator;
		weightedUris = disambiguator.disambiguate(GraphUtil.getUrisOfVertices(senses), subgraph);
	}

	public void checkWeightedUris() {
		for (WeightedSense wUri : weightedUris) {
			double expected = localMeasureResults.get(wUri.getSense()).get(disambiguator.getType());
			logger.info("uri: {} actual weight: {} expected weight: {}", wUri.getSense(), wUri.getWeight(), expected);
			Assert.assertEquals(expected, wUri.getWeight(), DELTA);
		}
	}

	@Override
	public void close() {
		super.close();
		if (subgraph != null)
			subgraph.shutdown();
	}

	public Map<String, Map<LocalConnectivityMeasure, Double>> getLocalMeasureResults() {
		return localMeasureResults;
	}

	public Graph getSubgraph() {
		return subgraph;
	}

	public List<WeightedSense> getWeightedUris() {
		return weightedUris;
	}

	private void setUpDisambiguationResults() throws IOException, URISyntaxException {
		String delimiterRegex = "\\s+";
		localMeasureResults = new HashMap<>();
		List<String> lines = FileUtils.readLinesFromFile(this.getClass(), NL_LOCAL_RESULTS);
		if (lines.isEmpty())
			throw new RuntimeException("test.results file shouldnt be empty.");

		String[] headers = lines.remove(0).split(delimiterRegex);

		for (String line : lines) {
			String[] values = line.split(delimiterRegex);
			String uri = values[0];

			Map<LocalConnectivityMeasure, Double> map = new EnumMap<>(LocalConnectivityMeasure.class);

			for (int i = 1; i < values.length; i++) {
				Double value = Double.parseDouble(values[i]);
				LocalConnectivityMeasure measure = LocalConnectivityMeasure.valueOf(headers[i]);
				map.put(measure, value);
			}

			localMeasureResults.put(uri, map);
		}

	}

}
