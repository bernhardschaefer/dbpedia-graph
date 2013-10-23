package de.unima.dws.dbpediagraph.graphdb;

import java.util.List;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unima.dws.dbpediagraph.graphdb.disambiguate.DisambiguatorHelper;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.LocalGraphDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.SurfaceFormSenseScore;

public class LocalDisambiguationTester implements DisambiguationTester {
	private static final Logger logger = LoggerFactory.getLogger(LocalDisambiguationTester.class);
	/** Name of the package where the local disambiguator classes reside. */
	private static final String LOCAL_PACKAGE_NAME = "de.unima.dws.dbpediagraph.graphdb.disambiguate.local";
	private static final double ALLOWED_SCORE_DEVIATION = 0.01;

	private final LocalGraphDisambiguator disambiguator;
	private final List<SurfaceFormSenseScore> actualDisambiguationResults;
	private final ExpectedDisambiguationResults expectedDisambiguationResults;

	public LocalDisambiguationTester(LocalGraphDisambiguator disambiguator, SubgraphTester subgraphData) {
		expectedDisambiguationResults = new ExpectedDisambiguationResults(TestSet.NavigliTestSet.NL_LOCAL_RESULTS,
				LOCAL_PACKAGE_NAME);
		this.disambiguator = disambiguator;
		actualDisambiguationResults = disambiguator.disambiguate(
				DisambiguatorHelper.transformVertices(subgraphData.allWordsSenses), subgraphData.getSubgraph());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unima.dws.dbpediagraph.graphdb.DisambiguationTestData#compareDisambiguationResults()
	 */
	@Override
	public void compareDisambiguationResults() {
		for (SurfaceFormSenseScore surfaceFormSenseScore : actualDisambiguationResults) {
			double expected = getExpectedDisambiguationResults().getResults().get(surfaceFormSenseScore.uri())
					.get(disambiguator.getClass());
			logger.info("uri: {} actual weight: {} expected weight: {}", surfaceFormSenseScore.uri(),
					surfaceFormSenseScore.getScore(), expected);
			Assert.assertEquals(expected, surfaceFormSenseScore.getScore(), ALLOWED_SCORE_DEVIATION);
		}
	}

	public List<SurfaceFormSenseScore> getActualDisambiguationResults() {
		return actualDisambiguationResults;
	}

	@Override
	public ExpectedDisambiguationResults getExpectedDisambiguationResults() {
		return expectedDisambiguationResults;
	}

}
