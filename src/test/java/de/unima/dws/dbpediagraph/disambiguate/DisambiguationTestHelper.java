package de.unima.dws.dbpediagraph.disambiguate;

import static org.junit.Assert.assertEquals;

import java.util.*;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graph.Graphs;
import de.unima.dws.dbpediagraph.graph.SubgraphTester;
import de.unima.dws.dbpediagraph.model.*;

/**
 * @author Bernhard Schäfer
 */
public class DisambiguationTestHelper {
	private static final Logger logger = LoggerFactory.getLogger(DisambiguationTestHelper.class);

	private static final String SENSES_DELIMITER = ",";
	private static final double ALLOWED_SCORE_DEVIATION = 0.01;

	/**
	 * Compare the expected and actual disambiguation results.
	 * 
	 * @param localDisambiguator
	 * @param subgraphData
	 * @param actualAllScoresResults
	 * 
	 * @throws AssertionError
	 *             if the expected and actual results differ
	 */
	public static <T extends SurfaceForm, U extends Sense> void compareAllLocalDisambiguationResults(
			LocalGraphDisambiguator<T, U> localDisambiguator, List<SurfaceFormSenseScore<T, U>> actualAllScoresResults,
			ExpectedDisambiguationResults<T, U> expectedDisambiguationResults, SubgraphTester subgraphData) {

		for (SurfaceFormSenseScore<T, U> surfaceFormSenseScore : actualAllScoresResults) {
			double expected = expectedDisambiguationResults.getRawResults()
					.get(surfaceFormSenseScore.getSense().fullUri()).get(localDisambiguator.getClass());
			logger.info("uri: {} actual weight: {} expected weight: {}", surfaceFormSenseScore.getSense().fullUri(),
					surfaceFormSenseScore.getScore(), expected);
			assertEquals(expected, surfaceFormSenseScore.getScore(), ALLOWED_SCORE_DEVIATION);
		}
	}

	public static <T extends SurfaceForm, U extends Sense> void compareAllGlobalDisambiguationResults(
			GlobalGraphDisambiguator<T, U> disambiguator,
			ExpectedDisambiguationResults<T, U> expectedDisambiguationData, SubgraphTester subgraphData) {

		for (Entry<String, Map<Class<?>, Double>> measureEntry : expectedDisambiguationData.getRawResults().entrySet()) {
			Collection<String> senseAssignments = DisambiguationTestHelper.split(measureEntry.getKey());
			Collection<Vertex> assignmentVertices = Graphs.verticesByFullUris(subgraphData.getSubgraph(),
					senseAssignments);

			// create sense graph based on the sense assignments
			double actualScore = disambiguator.globalConnectivityMeasure(assignmentVertices,
					subgraphData.getSubgraph(), subgraphData.senseVertices);

			double expectedScore = measureEntry.getValue().get(disambiguator.getClass());

			// for (Entry<Map<DefaultSurfaceForm, List<DefaultSense>>, Double> expected : expectedDisambiguationData
			// .getResultsForDisambiguator(disambiguator.getClass()).entrySet()) {
			// Map<DefaultSurfaceForm, List<DefaultSense>> assignment = expected.getKey();
			// double expectedScore = expected.getValue();
			//
			// Set<Vertex> assignmentVertices =
			// Sets.newHashSet(Iterables.concat(ModelTransformer.wordsVerticesFromSenses(
			// subgraphData.getSubgraph(), assignment)));
			//
			// // calculate actual score based on sense assignments
			// double actualScore = disambiguator.globalConnectivityMeasure(assignmentVertices,
			// subgraphData.getSubgraph(), subgraphData.senseVertices);

			logger.info("senses: {} actual score: {} expected score: {}", assignmentVertices, actualScore,
					expectedScore);
			assertEquals(expectedScore, actualScore, ALLOWED_SCORE_DEVIATION);
		}
	}

	public static void compareDisambiguatedAssignment(
			ExpectedDisambiguationResults<DefaultSurfaceForm, DefaultSense> allExpected,
			List<SurfaceFormSenseScore<DefaultSurfaceForm, DefaultSense>> actualAssignment, Class<?> disambiguatorClass) {
		List<SurfaceFormSenseScore<DefaultSurfaceForm, DefaultSense>> expectedAssignment = getHighestGlobalScoreResult(
				allExpected, disambiguatorClass);
		assertEquals(expectedAssignment.size(), actualAssignment.size());
		for (int i = 0; i < expectedAssignment.size(); i++)
			// equals surface form sense score is not applicable since there can be multiple assignments with max score
			assertEquals(String.format("Disambiguator: %s, Expected Assignment: %s, Actual Assignment: %s",
					disambiguatorClass.getSimpleName(), expectedAssignment, actualAssignment), expectedAssignment
					.get(i).getScore(), actualAssignment.get(i).getScore(), 0.01);
	}

	public static List<SurfaceFormSenseScore<DefaultSurfaceForm, DefaultSense>> getHighestGlobalScoreResult(
			ExpectedDisambiguationResults<DefaultSurfaceForm, DefaultSense> allExpected, Class<?> disambiguatorClass) {
		List<SurfaceFormSenseScore<DefaultSurfaceForm, DefaultSense>> highest = null;
		for (Entry<String, Map<Class<?>, Double>> entry : allExpected.getRawResults().entrySet()) {
			double expected = entry.getValue().get(disambiguatorClass);
			if (highest == null || expected >= highest.get(0).getScore()) {
				highest = transform(entry.getKey(), expected);
			}
		}
		return highest;
	}

	public static List<SurfaceFormSenseScore<DefaultSurfaceForm, DefaultSense>> transform(String senses, double score) {
		List<SurfaceFormSenseScore<DefaultSurfaceForm, DefaultSense>> res = new ArrayList<>();
		Collection<String> senseAssignments = split(senses);
		for (String sense : senseAssignments) {
			SurfaceFormSenseScore<DefaultSurfaceForm, DefaultSense> sfss = new SurfaceFormSenseScore<>(
					new DefaultSurfaceForm(senseNameToSurfaceFormName(sense)), new DefaultSense(sense), score);
			res.add(sfss);
		}
		return res;
	}

	private static String senseNameToSurfaceFormName(String sense) {
		// transforms drink1v --> DRINK; this is a bad hack :(
		return sense.substring(0, sense.length() - 2).toUpperCase();
	}

	public static Map<DefaultSurfaceForm, List<DefaultSense>> transform(String surfaceFormName, String senseNames) {
		Map<DefaultSurfaceForm, List<DefaultSense>> res = new HashMap<>();

		Collection<String> senseAssignments = split(senseNames);
		List<DefaultSense> senses = new ArrayList<>(senseAssignments.size());
		for (String senseName : senseAssignments)
			senses.add(new DefaultSense(senseName));
		res.put(new DefaultSurfaceForm(surfaceFormName), senses);

		return res;
	}

	public static Collection<String> split(String key) {
		return Arrays.asList(key.split(SENSES_DELIMITER));
	}

}
