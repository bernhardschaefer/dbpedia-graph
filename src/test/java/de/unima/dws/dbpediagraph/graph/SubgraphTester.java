package de.unima.dws.dbpediagraph.graph;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graph.GraphType;
import de.unima.dws.dbpediagraph.model.*;
import de.unima.dws.dbpediagraph.subgraph.*;
import de.unima.dws.dbpediagraph.util.FileUtils;

/**
 * Test a {@link SubgraphConstruction} implementation using a {@link TestSet}. Implementations are to be used in JUnit
 * tests.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class SubgraphTester {

	public static SubgraphConstructionSettings getNavigliSettings() {
		return new SubgraphConstructionSettings.Builder().graphType(GraphType.UNDIRECTED_GRAPH)
				.maxDistance(Integer.MAX_VALUE).build();
	}

	public static SubgraphTester newNavigliTester() {
		return newNavigliTester(getNavigliSettings());
	}

	public static SubgraphTester newNavigliTester(SubgraphConstructionSettings settings) {
		return new SubgraphTester(TestSet.NAVIGLI_FILE_NAMES, settings);
	}

	private final Graph graph;
	public final Collection<Set<Vertex>> surfaceFormSenseVertices;
	public final Set<Vertex> senseVertices;

	public final Map<DefaultSurfaceForm, List<DefaultSense>> surfaceFormSenses;

	public final List<String> expectedSubgraphEdges;
	public final List<String> expectedSubgraphVertices;
	private final Graph subgraph;
	private final SubgraphConstruction subgraphConstruction;

	public SubgraphTester(TestSet testSet) {
		this(testSet, SubgraphConstructionSettings.getDefault());
	}

	public SubgraphTester(TestSet testSet, SubgraphConstructionSettings settings) {
		try {
			graph = FileUtils.parseGraph(testSet.verticesFile, testSet.edgesFile, getClass());

			surfaceFormSenses = FileUtils.parseSurfaceFormSensesFromFile(testSet.sensesFile, getClass(), "",
					DefaultModelFactory.INSTANCE);

			surfaceFormSenseVertices = ModelTransformer.wordsVerticesFromSenses(graph, surfaceFormSenses);
			senseVertices = Sets.newHashSet(Iterables.concat(surfaceFormSenseVertices));

			expectedSubgraphVertices = FileUtils.readNonEmptyNonCommentLinesFromFile(getClass(), testSet.expectedVerticesFile);
			expectedSubgraphEdges = FileUtils.readNonEmptyNonCommentLinesFromFile(getClass(), testSet.expectedEdgesFile);
		} catch (IOException | URISyntaxException e) {
			throw new RuntimeException("Error while trying to construct test graph.", e);
		}

		this.subgraphConstruction = SubgraphConstructionFactory.newSubgraphConstruction(graph, settings);
		subgraph = subgraphConstruction.createSubgraph(surfaceFormSenseVertices);
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
