package de.unima.dws.dbpediagraph.graphdb;

import java.util.List;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unima.dws.dbpediagraph.graphdb.disambiguate.LocalGraphDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.model.DefaultModelFactory;
import de.unima.dws.dbpediagraph.graphdb.model.DefaultSense;
import de.unima.dws.dbpediagraph.graphdb.model.DefaultSurfaceForm;
import de.unima.dws.dbpediagraph.graphdb.model.ModelTransformer;
import de.unima.dws.dbpediagraph.graphdb.model.SurfaceFormSenseScore;

public class LocalDisambiguationTester implements DisambiguationTester {
	private static final Logger logger = LoggerFactory.getLogger(LocalDisambiguationTester.class);
	/** Name of the package where the local disambiguator classes reside. */
	private static final String LOCAL_PACKAGE_NAME = "de.unima.dws.dbpediagraph.graphdb.disambiguate.local";
	private static final double ALLOWED_SCORE_DEVIATION = 0.01;

	private final LocalGraphDisambiguator<DefaultSurfaceForm, DefaultSense> disambiguator;
	private final List<SurfaceFormSenseScore<DefaultSurfaceForm, DefaultSense>> actualDisambiguationResults;
	private final ExpectedDisambiguationResults expectedDisambiguationResults;

	public LocalDisambiguationTester(LocalGraphDisambiguator<DefaultSurfaceForm, DefaultSense> disambiguator,
			SubgraphTester subgraphData) {
		expectedDisambiguationResults = new ExpectedDisambiguationResults(TestSet.NavigliTestSet.NL_LOCAL_RESULTS,
				LOCAL_PACKAGE_NAME);
		this.disambiguator = disambiguator;
		actualDisambiguationResults = disambiguator.disambiguate(
				ModelTransformer.transformVertices(subgraphData.allWordsSenses, DefaultModelFactory.INSTANCE),
				subgraphData.getSubgraph());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unima.dws.dbpediagraph.graphdb.DisambiguationTestData#compareDisambiguationResults()
	 */
	@Override
	public void compareDisambiguationResults() {
		for (SurfaceFormSenseScore<DefaultSurfaceForm, DefaultSense> surfaceFormSenseScore : actualDisambiguationResults) {
			double expected = getExpectedDisambiguationResults().getResults()
					.get(surfaceFormSenseScore.sense().fullUri()).get(disambiguator.getClass());
			logger.info("uri: {} actual weight: {} expected weight: {}", surfaceFormSenseScore.sense().fullUri(),
					surfaceFormSenseScore.getScore(), expected);
			Assert.assertEquals(expected, surfaceFormSenseScore.getScore(), ALLOWED_SCORE_DEVIATION);
		}
	}

	public List<SurfaceFormSenseScore<DefaultSurfaceForm, DefaultSense>> getActualDisambiguationResults() {
		return actualDisambiguationResults;
	}

	@Override
	public ExpectedDisambiguationResults getExpectedDisambiguationResults() {
		return expectedDisambiguationResults;
	}

}
