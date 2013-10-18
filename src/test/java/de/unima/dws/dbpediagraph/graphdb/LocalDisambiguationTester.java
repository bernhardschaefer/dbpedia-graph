package de.unima.dws.dbpediagraph.graphdb;

import java.util.List;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unima.dws.dbpediagraph.graphdb.disambiguate.LocalDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.WeightedSense;

public class LocalDisambiguationTester implements DisambiguationTester {
	private static final Logger logger = LoggerFactory.getLogger(LocalDisambiguationTester.class);
	/** Name of the package where the local disambiguator classes reside. */
	private static final String LOCAL_PACKAGE_NAME = "de.unima.dws.dbpediagraph.graphdb.disambiguate.local";
	private static final double ALLOWED_SCORE_DEVIATION = 0.005;

	private final LocalDisambiguator disambiguator;
	private final List<WeightedSense> actualDisambiguationResults;
	private final ExpectedDisambiguationResults expectedDisambiguationResults;

	public LocalDisambiguationTester(LocalDisambiguator disambiguator, SubgraphTester subgraphData) {
		expectedDisambiguationResults = new ExpectedDisambiguationResults(TestSet.NavigliTestSet.NL_LOCAL_RESULTS,
				LOCAL_PACKAGE_NAME);
		this.disambiguator = disambiguator;
		actualDisambiguationResults = disambiguator.disambiguate(Graphs.getUrisOfVertices(subgraphData.allSenses),
				subgraphData.getSubgraph());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unima.dws.dbpediagraph.graphdb.DisambiguationTestData#compareDisambiguationResults()
	 */
	@Override
	public void compareDisambiguationResults() {
		for (WeightedSense wUri : actualDisambiguationResults) {
			double expected = getExpectedDisambiguationResults().getResults().get(wUri.getSense())
					.get(disambiguator.getClass());
			logger.info("uri: {} actual weight: {} expected weight: {}", wUri.getSense(), wUri.getWeight(), expected);
			Assert.assertEquals(expected, wUri.getWeight(), ALLOWED_SCORE_DEVIATION);
		}
	}

	public List<WeightedSense> getActualDisambiguationResults() {
		return actualDisambiguationResults;
	}

	@Override
	public ExpectedDisambiguationResults getExpectedDisambiguationResults() {
		return expectedDisambiguationResults;
	}

}
