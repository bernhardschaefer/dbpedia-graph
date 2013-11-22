package de.unima.dws.dbpediagraph.graphdb.disambiguate;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unima.dws.dbpediagraph.graphdb.disambiguate.GraphDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.model.Sense;
import de.unima.dws.dbpediagraph.graphdb.model.SurfaceForm;
import de.unima.dws.dbpediagraph.graphdb.util.FileUtils;

public class ExpectedDisambiguationResults<T extends SurfaceForm, U extends Sense> {
	private static final Logger logger = LoggerFactory.getLogger(ExpectedDisambiguationResults.class);

	private final Map<String, Map<Class<?>, Double>> expectedResults;

	private final Map<Class<?>, Map<Map<T, List<U>>, Double>> allExpectedResults;

	public ExpectedDisambiguationResults(String testResultsFileName, String packageNameDisambiguator) {
		expectedResults = createMeasureResults(testResultsFileName, packageNameDisambiguator);
		allExpectedResults = transform(expectedResults);
	}

	private static <T extends SurfaceForm, U extends Sense> Map<Class<?>, Map<Map<T, List<U>>, Double>> transform(
			Map<String, Map<Class<?>, Double>> rawResults) {
		Map<Class<?>, Map<Map<T, List<U>>, Double>> expectedResults = new HashMap<>();
		return expectedResults;
	}

	private Map<String, Map<Class<?>, Double>> createMeasureResults(String testResultsFileName,
			String packageNameDisambiguator) {
		try {
			return FileUtils.parseDisambiguationResults(testResultsFileName, this.getClass(), packageNameDisambiguator);
		} catch (IOException | URISyntaxException | ClassNotFoundException e) {
			logger.error(e.toString());
			throw new RuntimeException("Error while trying to construct test results.", e);
		}
	}

	/**
	 * @return the expected disambiguation results. The key of the outer map is the sense word, the inner map consists
	 *         of a {@link GraphDisambiguator} implementation as key and a corresponding score as value.
	 */
	public Map<String, Map<Class<?>, Double>> getRawResults() {
		return expectedResults;
	}

	/**
	 * DisambiguatorClass (Class<?>) --> Assignment (Map<T,List<U>>) --> Score (Double)
	 * 
	 * @return
	 */
	@Deprecated
	public Map<Class<?>, Map<Map<T, List<U>>, Double>> getAllResults() {
		return allExpectedResults;
	}

	@Deprecated
	public Map<Map<T, List<U>>, Double> getResultsForDisambiguator(Class<?> disambiguatorClass) {
		// TODO Auto-generated method stub
		return null;
	}
}
