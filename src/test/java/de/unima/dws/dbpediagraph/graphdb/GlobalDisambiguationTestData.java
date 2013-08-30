package de.unima.dws.dbpediagraph.graphdb;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unima.dws.dbpediagraph.graphdb.disambiguate.ConnectivityMeasure;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.GlobalDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstruction;

public class GlobalDisambiguationTestData extends AbstractDisambiguationTestData {
	private static final Logger logger = LoggerFactory.getLogger(GlobalDisambiguationTestData.class);

	/** Global Connectivity Measure Results for the example from Navigli&Lapata (2010) */
	private static final String NL_GLOBAL_RESULTS = "/nl-global-test.results";

	private static final String SENSES_DELIMITER = ",";

	private final GlobalDisambiguator disambiguator;

	public GlobalDisambiguationTestData(GlobalDisambiguator disambiguator, SubgraphConstruction subgraphConstruction) {
		super(subgraphConstruction, NL_GLOBAL_RESULTS);
		this.disambiguator = disambiguator;
	}

	@Override
	public void checkDisambiguationResults() {
		for (Entry<String, Map<ConnectivityMeasure, Double>> measureEntry : measureResults.entrySet()) {
			String[] senses = measureEntry.getKey().split(SENSES_DELIMITER);

			Map<ConnectivityMeasure, Double> map = measureEntry.getValue();
			double expected = map.get(disambiguator.getType());

			double actual = disambiguator.globalConnectivityMeasure(Arrays.asList(senses), subgraph);
			logger.info("senses: {} actual score: {} expected score: {}", senses, actual, expected);
			Assert.assertEquals(expected, actual, DELTA);
		}
	}
}
