package de.unima.dws.dbpediagraph.graphdb;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Graph;

import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstruction;
import de.unima.dws.dbpediagraph.graphdb.util.FileUtils;

public abstract class AbstractDisambiguationTestData extends SubgraphTestData {
	protected static final double DELTA = 0.005;

	protected final Graph subgraph;

	protected Map<String, Map<Class<?>, Double>> measureResults;

	private static final Logger logger = LoggerFactory.getLogger(AbstractDisambiguationTestData.class);

	public AbstractDisambiguationTestData(SubgraphConstruction subgraphConstruction, String testResultsFileName,
			String packageNameDisambiguator) {
		super();
		subgraphConstruction.setGraph(graph);
		subgraph = subgraphConstruction.createSubgraph(allWordsSenses);

		measureResults = createMeasureResults(testResultsFileName, packageNameDisambiguator);
	}

	public abstract void checkDisambiguationResults();

	@Override
	public void close() {
		super.close();
		if (subgraph != null)
			subgraph.shutdown();
	}

	protected Map<String, Map<Class<?>, Double>> createMeasureResults(String testResultsFileName,
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

	public Graph getSubgraph() {
		return subgraph;
	}

}
