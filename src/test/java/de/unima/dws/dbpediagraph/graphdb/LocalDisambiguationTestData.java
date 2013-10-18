package de.unima.dws.dbpediagraph.graphdb;

import java.util.List;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unima.dws.dbpediagraph.graphdb.disambiguate.LocalDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.WeightedSense;

public class LocalDisambiguationTestData extends AbstractDisambiguationTestData {
	private static final Logger logger = LoggerFactory.getLogger(LocalDisambiguationTestData.class);

	/** Name of the package where the local disambiguator classes reside. */
	private static final String LOCAL_PACKAGE_NAME = "de.unima.dws.dbpediagraph.graphdb.disambiguate.local";

	private final LocalDisambiguator disambiguator;
	protected List<WeightedSense> weightedUris;

	private final ExpectedDisambiguationTestData expectedDisambiguationResults;

	public LocalDisambiguationTestData(LocalDisambiguator disambiguator, SubgraphTester subgraphData) {
		expectedDisambiguationResults = new ExpectedDisambiguationTestData(TestSet.NavigliTestSet.NL_LOCAL_RESULTS,
				LOCAL_PACKAGE_NAME);
		this.disambiguator = disambiguator;
		weightedUris = disambiguator.disambiguate(GraphUtil.getUrisOfVertices(subgraphData.allSenses),
				subgraphData.getSubgraph());
	}

	@Override
	public void checkDisambiguationResults() {
		for (WeightedSense wUri : weightedUris) {
			double expected = expectedDisambiguationResults.getMeasureResults().get(wUri.getSense())
					.get(disambiguator.getClass());
			logger.info("uri: {} actual weight: {} expected weight: {}", wUri.getSense(), wUri.getWeight(), expected);
			Assert.assertEquals(expected, wUri.getWeight(), DELTA);
		}
	}

	public List<WeightedSense> getWeightedUris() {
		return weightedUris;
	}

}
