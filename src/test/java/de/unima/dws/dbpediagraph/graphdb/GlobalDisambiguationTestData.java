package de.unima.dws.dbpediagraph.graphdb;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.disambiguate.GlobalDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstruction;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstructionFactory;
import de.unima.dws.dbpediagraph.graphdb.util.CollectionUtils;

public class GlobalDisambiguationTestData extends AbstractDisambiguationTestData {
	private static final Logger logger = LoggerFactory.getLogger(GlobalDisambiguationTestData.class);

	/** Global Connectivity Measure Results for the example from Navigli&Lapata (2010) */
	private static final String NL_GLOBAL_RESULTS = SubgraphTestData.NL_PKG + "/nl-global-test.results";

	/** Name of the package where the local disambiguator classes reside. */
	private static final String GLOBAL_PACKAGE_NAME = "de.unima.dws.dbpediagraph.graphdb.disambiguate.global";

	private static final String SENSES_DELIMITER = ",";

	private final GlobalDisambiguator disambiguator;
	private final SubgraphConstruction subgraphConstruction;

	public GlobalDisambiguationTestData(GlobalDisambiguator disambiguator) {
		this(disambiguator, SubgraphConstructionFactory.newDefaultImplementation());
	}

	public GlobalDisambiguationTestData(GlobalDisambiguator disambiguator, SubgraphConstruction subgraphConstruction) {
		super(subgraphConstruction, NL_GLOBAL_RESULTS, GLOBAL_PACKAGE_NAME);
		this.disambiguator = disambiguator;

		subgraphConstruction.setGraph(subgraph);
		this.subgraphConstruction = subgraphConstruction;
	}

	@Override
	public void checkDisambiguationResults() {
		for (Entry<String, Map<Class<?>, Double>> measureEntry : measureResults.entrySet()) {
			Collection<String> senseAssignments = Arrays.asList(measureEntry.getKey().split(SENSES_DELIMITER));
			Collection<Collection<Vertex>> wordsSenses = CollectionUtils.split(GraphUtil.getVerticesByUri(subgraph,
					senseAssignments));

			// create sense graph based on the sense assignments
			Graph sensegraph = subgraphConstruction.createSubgraphFromSenses(wordsSenses);
			double actual = disambiguator.globalConnectivityMeasure(senseAssignments, sensegraph);
			sensegraph.shutdown();

			double expected = measureEntry.getValue().get(disambiguator.getClass());

			logger.info("senses: {} actual score: {} expected score: {}", senseAssignments, actual, expected);
			Assert.assertEquals(expected, actual, DELTA);
		}
	}
}
