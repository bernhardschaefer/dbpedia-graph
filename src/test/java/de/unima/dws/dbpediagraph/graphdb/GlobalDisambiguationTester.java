package de.unima.dws.dbpediagraph.graphdb;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.disambiguate.GlobalGraphDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.model.DefaultSense;
import de.unima.dws.dbpediagraph.graphdb.model.DefaultSurfaceForm;

public class GlobalDisambiguationTester implements DisambiguationTester {
	private static final Logger logger = LoggerFactory.getLogger(GlobalDisambiguationTester.class);

	/** Name of the package where the local disambiguator classes reside. */
	private static final String GLOBAL_PACKAGE_NAME = "de.unima.dws.dbpediagraph.graphdb.disambiguate.global";
	private static final String SENSES_DELIMITER = ",";
	private static final double ALLOWED_SCORE_DEVIATION = 0.01;

	private final GlobalGraphDisambiguator<DefaultSurfaceForm, DefaultSense> disambiguator;

	private final ExpectedDisambiguationResults expectedDisambiguationData;

	private final SubgraphTester subgraphData;

	public GlobalDisambiguationTester(GlobalGraphDisambiguator<DefaultSurfaceForm, DefaultSense> disambiguator,
			SubgraphTester subgraphData) {
		expectedDisambiguationData = new ExpectedDisambiguationResults(TestSet.NavigliTestSet.NL_GLOBAL_RESULTS,
				GLOBAL_PACKAGE_NAME);
		this.disambiguator = disambiguator;
		this.subgraphData = subgraphData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unima.dws.dbpediagraph.graphdb.DisambiguationTestData#
	 * compareDisambiguationResults()
	 */
	@Override
	public void compareDisambiguationResults() {
		for (Entry<String, Map<Class<?>, Double>> measureEntry : expectedDisambiguationData.getResults().entrySet()) {
			Collection<String> senseAssignments = Arrays.asList(measureEntry.getKey().split(SENSES_DELIMITER));
			Collection<Vertex> senses = Graphs.verticesByUri(subgraphData.getSubgraph(), senseAssignments);

			// create sense graph based on the sense assignments
			double actual = disambiguator.globalConnectivityMeasure(senses, subgraphData.getSubgraph());

			double expected = measureEntry.getValue().get(disambiguator.getClass());

			logger.info("senses: {} actual score: {} expected score: {}", senseAssignments, actual, expected);
			Assert.assertEquals(expected, actual, ALLOWED_SCORE_DEVIATION);
		}
	}

	@Override
	public ExpectedDisambiguationResults getExpectedDisambiguationResults() {
		return expectedDisambiguationData;
	}
}
