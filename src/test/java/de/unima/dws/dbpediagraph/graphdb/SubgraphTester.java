package de.unima.dws.dbpediagraph.graphdb;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstruction;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstructionFactory;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstructionSettings;
import de.unima.dws.dbpediagraph.graphdb.util.CollectionUtils;
import de.unima.dws.dbpediagraph.graphdb.util.FileUtils;

/**
 * Test a {@link SubgraphConstruction} implementation using a {@link TestSet}. Implementations are to be used in JUnit
 * tests.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class SubgraphTester {

	public static final SubgraphTester newNavigliTestData() {
		return new SubgraphTester(TestSet.NAVIGLI_FILE_NAMES, SubgraphConstructionFactory.defaultClass());
	}

	private final Graph graph;
	public final Collection<Collection<Vertex>> allWordsSenses;
	public final Collection<Vertex> allSenses;

	public final List<String> expectedSubgraphEdges;
	public final List<String> expectedSubgraphVertices;
	private final Graph subgraph;
	private final SubgraphConstruction subgraphConstruction;

	public SubgraphTester(TestSet testSet, Class<? extends SubgraphConstruction> subgraphConstructionClass) {
		this(testSet, subgraphConstructionClass, SubgraphConstructionSettings.getDefault());
	}

	public SubgraphTester(TestSet testSet, Class<? extends SubgraphConstruction> subgraphConstructionClass,
			SubgraphConstructionSettings settings) {
		try {
			graph = FileUtils.parseGraph(testSet.verticesFile, testSet.edgesFile, getClass());
			allWordsSenses = FileUtils.parseAllWordsSenses(graph, testSet.sensesFile, getClass(), "");
			allSenses = CollectionUtils.combine(allWordsSenses);

			expectedSubgraphVertices = FileUtils.readRelevantLinesFromFile(getClass(), testSet.expectedVerticesFile);
			expectedSubgraphEdges = FileUtils.readRelevantLinesFromFile(getClass(), testSet.expectedEdgesFile);
		} catch (IOException | URISyntaxException e) {
			throw new RuntimeException("Error while trying to construct test graph.", e);
		}

		subgraphConstruction = SubgraphConstructionFactory.newInstance(subgraphConstructionClass, settings, graph);
		subgraph = getSubgraphConstruction().createSubgraph(allWordsSenses);
	}

	public void close() {
		if (subgraph != null)
			subgraph.shutdown();
		if (graph != null)
			graph.shutdown();
	}

	public Graph getSubgraph() {
		return subgraph;
	}

	public SubgraphConstruction getSubgraphConstruction() {
		return subgraphConstruction;
	}

}
