package de.unima.dws.dbpediagraph.graphdb;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unima.dws.dbpediagraph.graphdb.disambiguate.GraphDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.LocalGraphDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.model.DefaultModelFactory;
import de.unima.dws.dbpediagraph.graphdb.model.DefaultSense;
import de.unima.dws.dbpediagraph.graphdb.model.DefaultSurfaceForm;
import de.unima.dws.dbpediagraph.graphdb.model.ModelTransformer;
import de.unima.dws.dbpediagraph.graphdb.model.SurfaceFormSenseScore;
import de.unima.dws.dbpediagraph.graphdb.util.CollectionUtils;

/**
 * Tests a {@link GraphDisambiguator} class using a {@link SubgraphTester} and a
 * {@link TestSet}. Is used in JUnit tests.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class LocalDisambiguationTester {
	private static final Logger logger = LoggerFactory.getLogger(LocalDisambiguationTester.class);
	/** Name of the package where the local disambiguator classes reside. */
	private static final String LOCAL_PACKAGE_NAME = "de.unima.dws.dbpediagraph.graphdb.disambiguate.local";
	private static final double ALLOWED_SCORE_DEVIATION = 0.01;

	private final LocalGraphDisambiguator<DefaultSurfaceForm, DefaultSense> localDisambiguator;
	private final List<SurfaceFormSenseScore<DefaultSurfaceForm, DefaultSense>> actualAllScoresResults;
	private final ExpectedDisambiguationResults expectedDisambiguationResults;
	private final List<SurfaceFormSenseScore<DefaultSurfaceForm, DefaultSense>> actualDisambiguationResults;

	public LocalDisambiguationTester(LocalGraphDisambiguator<DefaultSurfaceForm, DefaultSense> localDisambiguator,
			SubgraphTester subgraphData) {
		expectedDisambiguationResults = new ExpectedDisambiguationResults(TestSet.NavigliTestSet.NL_LOCAL_RESULTS,
				LOCAL_PACKAGE_NAME);
		this.localDisambiguator = localDisambiguator;

		Map<DefaultSurfaceForm, List<DefaultSense>> sFSs = ModelTransformer.surfaceFormSensesFromVertices(
				subgraphData.allWordsSenses, DefaultModelFactory.INSTANCE);
		actualDisambiguationResults = localDisambiguator.disambiguate(sFSs, subgraphData.getSubgraph());
		actualAllScoresResults = CollectionUtils.joinListValues(localDisambiguator.allSurfaceFormSensesScores(sFSs,
				subgraphData.getSubgraph()));
	}

	/**
	 * Compare the expected and actual disambiguation results.
	 * 
	 * @throws AssertionError
	 *             if the expected and actual results differ
	 */
	public void compareDisambiguationResults() {
		for (SurfaceFormSenseScore<DefaultSurfaceForm, DefaultSense> surfaceFormSenseScore : actualAllScoresResults) {
			double expected = expectedDisambiguationResults.getResults().get(surfaceFormSenseScore.sense().fullUri())
					.get(localDisambiguator.getClass());
			logger.info("uri: {} actual weight: {} expected weight: {}", surfaceFormSenseScore.sense().fullUri(),
					surfaceFormSenseScore.score(), expected);
			Assert.assertEquals(expected, surfaceFormSenseScore.score(), ALLOWED_SCORE_DEVIATION);
		}
	}

	public List<SurfaceFormSenseScore<DefaultSurfaceForm, DefaultSense>> getActualAllScoresResults() {
		return actualAllScoresResults;
	}

	public List<SurfaceFormSenseScore<DefaultSurfaceForm, DefaultSense>> getActualDisambiguationResults() {
		return actualDisambiguationResults;
	}

	public ExpectedDisambiguationResults getExpectedDisambiguationResults() {
		return expectedDisambiguationResults;
	}

}
