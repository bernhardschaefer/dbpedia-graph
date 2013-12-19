package de.unima.dws.dbpediagraph.disambiguate;

import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.*;

import de.unima.dws.dbpediagraph.disambiguate.local.*;
import de.unima.dws.dbpediagraph.graph.*;
import de.unima.dws.dbpediagraph.model.*;
import de.unima.dws.dbpediagraph.util.CollectionUtils;
import de.unima.dws.dbpediagraph.weights.DummyEdgeWeights;
import de.unima.dws.dbpediagraph.weights.EdgeWeights;

public class TestLocalDisambiguators {
	/** Name of the package where the local disambiguator classes reside. */
	private static final String LOCAL_PACKAGE_NAME = "de.unima.dws.dbpediagraph.disambiguate.local";

	private static final SubgraphTester subgraphNavigli = SubgraphTester.newNavigliTester();;
	private static final ExpectedDisambiguationResults<DefaultSurfaceForm, DefaultSense> expectedResults = new ExpectedDisambiguationResults<>(
			TestSet.NavigliTestSet.NL_LOCAL_RESULTS, LOCAL_PACKAGE_NAME);

	private static Map<LocalGraphDisambiguator<DefaultSurfaceForm, DefaultSense>, List<SurfaceFormSenseScore<DefaultSurfaceForm, DefaultSense>>> disambiguatorResults;

	@BeforeClass
	public static void beforeClass() throws ConfigurationException {
		GraphType graphType = GraphType.UNDIRECTED_GRAPH;
		EdgeWeights graphWeights = DummyEdgeWeights.INSTANCE;
		boolean usePriorFallback = false;

		List<LocalGraphDisambiguator<DefaultSurfaceForm, DefaultSense>> localDisambiguators = new ArrayList<>();
		localDisambiguators.add(new BetweennessCentrality<DefaultSurfaceForm, DefaultSense>(graphType, graphWeights,
				usePriorFallback));
		localDisambiguators.add(new DegreeCentrality<DefaultSurfaceForm, DefaultSense>(graphType, graphWeights,
				usePriorFallback));
		// double alpha = 0;
		// int iterations = 100;
		// localDisambiguators.add(new HITSCentrality<DefaultSurfaceForm, DefaultSense>(graphType, graphWeights, alpha,
		// iterations));
		localDisambiguators.add(new KPPCentrality<DefaultSurfaceForm, DefaultSense>(graphType, graphWeights,
				usePriorFallback));
		// localDisambiguators.add(new PageRankCentrality<DefaultSurfaceForm, DefaultSense>(graphType, graphWeights));

		disambiguatorResults = new HashMap<>();
		for (LocalGraphDisambiguator<DefaultSurfaceForm, DefaultSense> disambiguator : localDisambiguators) {
			List<SurfaceFormSenseScore<DefaultSurfaceForm, DefaultSense>> actualAllScoresResults = CollectionUtils
					.joinListValues(disambiguator.allSurfaceFormSensesScores(subgraphNavigli.surfaceFormSenses,
							subgraphNavigli.getSubgraph()));
			disambiguatorResults.put(disambiguator, actualAllScoresResults);
		}

	}

	@AfterClass
	public static void afterClass() {
		if (subgraphNavigli != null)
			subgraphNavigli.close();
	}

	@Test
	public void testCalculatedCentralityValues() {
		for (Entry<LocalGraphDisambiguator<DefaultSurfaceForm, DefaultSense>, List<SurfaceFormSenseScore<DefaultSurfaceForm, DefaultSense>>> entry : disambiguatorResults
				.entrySet()) {
			DisambiguationTestHelper.compareAllLocalDisambiguationResults(entry.getKey(), entry.getValue(),
					expectedResults, subgraphNavigli);
		}
	}

}
