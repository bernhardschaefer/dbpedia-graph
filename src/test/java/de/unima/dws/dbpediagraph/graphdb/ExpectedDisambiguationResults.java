package de.unima.dws.dbpediagraph.graphdb;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unima.dws.dbpediagraph.graphdb.disambiguate.Disambiguator;
import de.unima.dws.dbpediagraph.graphdb.util.FileUtils;

public class ExpectedDisambiguationResults {
	private static final Logger logger = LoggerFactory.getLogger(ExpectedDisambiguationResults.class);

	private final Map<String, Map<Class<?>, Double>> expectedResults;

	public ExpectedDisambiguationResults(String testResultsFileName, String packageNameDisambiguator) {
		expectedResults = createMeasureResults(testResultsFileName, packageNameDisambiguator);
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
	 *         of a {@link Disambiguator} implementation as key and a corresponding score as value.
	 */
	public Map<String, Map<Class<?>, Double>> getResults() {
		return expectedResults;
	}
}
