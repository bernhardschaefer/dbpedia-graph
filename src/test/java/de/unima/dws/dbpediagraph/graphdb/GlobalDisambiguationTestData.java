package de.unima.dws.dbpediagraph.graphdb;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Graph;

import de.unima.dws.dbpediagraph.graphdb.disambiguate.ConnectivityMeasure;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.GlobalDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstruction;

public class GlobalDisambiguationTestData extends AbstractDisambiguationTestData {
	private static final Logger logger = LoggerFactory.getLogger(GlobalDisambiguationTestData.class);

	/** Global Connectivity Measure Results for the example from Navigli&Lapata (2010) */
	private static final String NL_GLOBAL_RESULTS = "/nl-global-test.results";

	private static final String SENSES_DELIMITER = ",";

	private final GlobalDisambiguator disambiguator;
	private final SubgraphConstruction subgraphConstruction;

	public GlobalDisambiguationTestData(GlobalDisambiguator disambiguator, SubgraphConstruction subgraphConstruction) {
		super(subgraphConstruction, NL_GLOBAL_RESULTS);
		this.disambiguator = disambiguator;

		subgraphConstruction.setGraph(subgraph);
		this.subgraphConstruction = subgraphConstruction;
	}

	@Override
	public void checkDisambiguationResults() {
		for (Entry<String, Map<ConnectivityMeasure, Double>> measureEntry : measureResults.entrySet()) {
			List<String> senseAssignments = Arrays.asList(measureEntry.getKey().split(SENSES_DELIMITER));

			// create sense graph based on the sense assignments
			Graph sensegraph = subgraphConstruction.createSubgraph(GraphUtil.getVerticesByUri(subgraph,
					senseAssignments));
			double actual = disambiguator.globalConnectivityMeasure(senseAssignments, sensegraph);
			sensegraph.shutdown();

			double expected = measureEntry.getValue().get(disambiguator.getType());

			logger.info("senses: {} actual score: {} expected score: {}", senseAssignments, actual, expected);
			Assert.assertEquals(expected, actual, DELTA);
		}
	}
}
