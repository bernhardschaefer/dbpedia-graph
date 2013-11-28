package de.unima.dws.dbpediagraph.disambiguate;

import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unima.dws.dbpediagraph.disambiguate.global.*;
import de.unima.dws.dbpediagraph.graph.SubgraphTester;
import de.unima.dws.dbpediagraph.graph.TestSet;
import de.unima.dws.dbpediagraph.model.*;
import de.unima.dws.dbpediagraph.subgraph.SubgraphConstructionSettings;
import de.unima.dws.dbpediagraph.weights.EdgeWeights;
import de.unima.dws.dbpediagraph.weights.EdgeWeightsFactory;

public class TestGlobalDisambiguators {
	private static final Logger logger = LoggerFactory.getLogger(TestGlobalDisambiguators.class);

	/** Name of the package where the local disambiguator classes reside. */
	private static final String GLOBAL_PACKAGE_NAME = "de.unima.dws.dbpediagraph.disambiguate.global";

	private static SubgraphTester subgraphTesterNavigli;
	private static ExpectedDisambiguationResults<DefaultSurfaceForm, DefaultSense> expectedResults;
	private static Map<GlobalGraphDisambiguator<DefaultSurfaceForm, DefaultSense>, List<SurfaceFormSenseScore<DefaultSurfaceForm, DefaultSense>>> disambiguatorResults;

	@BeforeClass
	public static void setUp() throws ConfigurationException {

		SubgraphConstructionSettings settings = SubgraphTester.getNavigliSettings();
		subgraphTesterNavigli = SubgraphTester.newNavigliTester(settings);
		EdgeWeights graphWeights = EdgeWeightsFactory.dbpediaImplFromConfig(new PropertiesConfiguration(
				TestSet.NavigliTestSet.NL_TEST_PROPERTIES));

		List<GlobalGraphDisambiguator<DefaultSurfaceForm, DefaultSense>> disambiguators = new ArrayList<>();
		disambiguators.add(new Compactness<DefaultSurfaceForm, DefaultSense>(settings, graphWeights));
		disambiguators.add(new EdgeDensity<DefaultSurfaceForm, DefaultSense>(settings, graphWeights));
		disambiguators.add(new GraphEntropy<DefaultSurfaceForm, DefaultSense>(settings, graphWeights));

		disambiguatorResults = new HashMap<>();
		for (GlobalGraphDisambiguator<DefaultSurfaceForm, DefaultSense> disambiguator : disambiguators) {
			List<SurfaceFormSenseScore<DefaultSurfaceForm, DefaultSense>> actualDisambiguationResults = disambiguator
					.disambiguate(subgraphTesterNavigli.surfaceFormSenses, subgraphTesterNavigli.getSubgraph());
			disambiguatorResults.put(disambiguator, actualDisambiguationResults);
		}

		expectedResults = new ExpectedDisambiguationResults<>(TestSet.NavigliTestSet.NL_GLOBAL_RESULTS,
				GLOBAL_PACKAGE_NAME);

	}

	@AfterClass
	public static void tearDown() {
		if (subgraphTesterNavigli != null)
			subgraphTesterNavigli.close();
	}

	@Test
	public void testCalculatedConnectivityMeasures() {
		for (GlobalGraphDisambiguator<DefaultSurfaceForm, DefaultSense> disambiguator : disambiguatorResults.keySet()) {
			logger.info("Testing connectivity measure scores with {}", disambiguator);
			DisambiguationTestHelper.compareAllGlobalDisambiguationResults(disambiguator, expectedResults,
					subgraphTesterNavigli);
		}
	}

	@Test
	public void testDisambiguation() {
		for (Entry<GlobalGraphDisambiguator<DefaultSurfaceForm, DefaultSense>, List<SurfaceFormSenseScore<DefaultSurfaceForm, DefaultSense>>> entry : disambiguatorResults
				.entrySet()) {
			GlobalGraphDisambiguator<DefaultSurfaceForm, DefaultSense> disambiguator = entry.getKey();
			logger.info("Testing disambiguation with {}", disambiguator);
			DisambiguationTestHelper.compareDisambiguatedAssignment(expectedResults, entry.getValue(),
					disambiguator.getClass());
		}
	}

}
