package de.unima.dws.dbpediagraph.graphdb;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unima.dws.dbpediagraph.graphdb.util.FileUtils;

public class ExpectedDisambiguationTestData {
	private static final Logger logger = LoggerFactory.getLogger(ExpectedDisambiguationTestData.class);

	private final Map<String, Map<Class<?>, Double>> measureResults;

	public ExpectedDisambiguationTestData(String testResultsFileName, String packageNameDisambiguator) {
		measureResults = createMeasureResults(testResultsFileName, packageNameDisambiguator);
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

	public Map<String, Map<Class<?>, Double>> getMeasureResults() {
		return measureResults;
	}
}
