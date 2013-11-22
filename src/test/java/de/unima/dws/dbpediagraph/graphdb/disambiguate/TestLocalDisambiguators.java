package de.unima.dws.dbpediagraph.graphdb.disambiguate;

import java.util.*;
import java.util.Map.Entry;

import org.junit.*;

import de.unima.dws.dbpediagraph.graphdb.*;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.local.*;
import de.unima.dws.dbpediagraph.graphdb.model.*;
import de.unima.dws.dbpediagraph.graphdb.util.CollectionUtils;

public class TestLocalDisambiguators {
	/** Name of the package where the local disambiguator classes reside. */
	private static final String LOCAL_PACKAGE_NAME = "de.unima.dws.dbpediagraph.graphdb.disambiguate.local";

	private static final SubgraphTester subgraphNavigli = SubgraphTester.newNavigliTester();;
	private static final DefaultModelFactory factory = DefaultModelFactory.INSTANCE;
	private static final ExpectedDisambiguationResults<DefaultSurfaceForm, DefaultSense> expectedResults = new ExpectedDisambiguationResults<>(
			TestSet.NavigliTestSet.NL_LOCAL_RESULTS, LOCAL_PACKAGE_NAME);

	private static Map<LocalGraphDisambiguator<DefaultSurfaceForm, DefaultSense>, List<SurfaceFormSenseScore<DefaultSurfaceForm, DefaultSense>>> disambiguatorResults;

	@BeforeClass
	public static void beforeClass() {
		GraphType graphType = GraphType.UNDIRECTED_GRAPH;

		List<LocalGraphDisambiguator<DefaultSurfaceForm, DefaultSense>> localDisambiguators = new ArrayList<>();
		localDisambiguators.add(new BetweennessCentrality<>(graphType, factory));
		localDisambiguators.add(new DegreeCentrality<>(graphType, factory));
		// localDisambiguators.add(new HITSCentrality<>(graphType, factory));
		localDisambiguators.add(new KPPCentrality<>(graphType, factory));
		// localDisambiguators.add(new PageRankCentrality<>(graphType, factory));

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
