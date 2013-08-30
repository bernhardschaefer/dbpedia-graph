package de.unima.dws.dbpediagraph.graphdb;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import com.tinkerpop.blueprints.Graph;

import de.unima.dws.dbpediagraph.graphdb.disambiguate.ConnectivityMeasure;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstruction;
import de.unima.dws.dbpediagraph.graphdb.util.FileUtils;

public abstract class AbstractDisambiguationTestData extends SubgraphTestData {
	protected static final double DELTA = 0.005;

	protected final Graph subgraph;

	protected Map<String, Map<ConnectivityMeasure, Double>> measureResults;

	public AbstractDisambiguationTestData(SubgraphConstruction subgraphConstruction, String testResultsFileName) {
		super();
		subgraphConstruction.setGraph(graph);
		subgraph = subgraphConstruction.createSubgraph(allWordsSenses);

		measureResults = createMeasureResults(testResultsFileName);
	}

	public abstract void checkDisambiguationResults();

	@Override
	public void close() {
		super.close();
		if (subgraph != null)
			subgraph.shutdown();
	}

	protected Map<String, Map<ConnectivityMeasure, Double>> createMeasureResults(String testResultsFileName) {
		try {
			return FileUtils.parseDisambiguationResults(testResultsFileName, this.getClass());
		} catch (IOException | URISyntaxException e) {
			throw new RuntimeException("Error while trying to construct test results.", e);
		}
	}

	public Map<String, Map<ConnectivityMeasure, Double>> getMeasureResults() {
		return measureResults;
	}

	public Graph getSubgraph() {
		return subgraph;
	}

}
