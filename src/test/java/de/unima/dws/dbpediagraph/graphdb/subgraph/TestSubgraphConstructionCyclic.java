package de.unima.dws.dbpediagraph.graphdb.subgraph;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.GraphUtil;
import de.unima.dws.dbpediagraph.graphdb.util.FileUtils;

public class TestSubgraphConstructionCyclic {

	private static final String PKG = "/test-cyclic";

	private static final int MAX_DISTANCE = 10;

	private Graph graph;
	private Graph subGraph;

	private Collection<String> expectedVertices;
	private Collection<String> expectedEdges;

	@Before
	public void setUp() throws IOException, URISyntaxException {
		graph = FileUtils.parseGraph(PKG + "/vertices", PKG + "/edges", getClass());

		SubgraphConstruction sc = SubgraphConstructionFactory.newDefaultImplementation(graph,
				new SubgraphConstructionSettings().maxDistance(MAX_DISTANCE));

		Collection<Collection<Vertex>> wordsSenses = GraphUtil.getWordsVerticesByUri(graph,
				FileUtils.readUrisFromFile(getClass(), PKG + "/senses", ""));
		subGraph = sc.createSubgraphFromSenses(wordsSenses);

		expectedVertices = FileUtils.readRelevantLinesFromFile(getClass(), PKG + "/subgraph-vertices");
		expectedEdges = FileUtils.readRelevantLinesFromFile(getClass(), PKG + "/subgraph-edges");
	}

	@After
	public void tearDown() {
		subGraph.shutdown();
		graph.shutdown();
	}

	@Test
	public void testContainedEdges() {
		for (String edgeName : expectedEdges) {
			Assert.assertNotNull("Edge should be contained in subgraph: " + edgeName,
					subGraph.getEdge(FileUtils.lineToLabel(edgeName)));
		}
	}

	@Test
	public void testContainedVertices() {
		for (String vertexName : expectedVertices) {
			Assert.assertNotNull("Vertex should be contained in subgraph: " + vertexName,
					subGraph.getVertex(vertexName));
		}
	}

	@Test
	public void testNumberOfEdges() {
		assertEquals(expectedEdges.size(), GraphUtil.getNumberOfEdges(subGraph));
	}

	@Test
	public void testNumberOfVertices() {
		assertEquals(expectedVertices.size(), GraphUtil.getNumberOfVertices(subGraph));
	}

}
