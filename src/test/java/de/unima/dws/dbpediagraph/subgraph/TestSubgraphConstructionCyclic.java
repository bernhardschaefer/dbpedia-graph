package de.unima.dws.dbpediagraph.subgraph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.*;

import de.unima.dws.dbpediagraph.graph.*;
import de.unima.dws.dbpediagraph.util.FileUtils;
import de.unima.dws.dbpediagraph.weights.DummyEdgeWeights;
import de.unima.dws.dbpediagraph.weights.EdgeWeights;

/**
 * @author Bernhard Schäfer
 */
public class TestSubgraphConstructionCyclic {
	private static final int MAX_DISTANCE = 10;
	private static final EdgeWeights EDGE_WEIGHTS = DummyEdgeWeights.INSTANCE;

	private static SubgraphTester subgraphTesterDirected;

	// private static SubgraphTester subgraphTesterUndirected;

	@BeforeClass
	public static void setUpBeforeClass() throws IOException, URISyntaxException {
		subgraphTesterDirected = new SubgraphTester(TestSet.CYCLIC_FILE_NAMES,
				new SubgraphConstructionSettings.Builder().graphType(GraphType.DIRECTED_GRAPH)
						.maxDistance(MAX_DISTANCE).build());
		// subgraphTesterUndirected = new SubgraphTester(TestSet.CYCLIC_FILE_NAMES, new
		// SubgraphConstructionSettings.Builder()
		// .graphType(GraphType.UNDIRECTED_GRAPH).maxDistance(MAX_DISTANCE).build());
	}

	@AfterClass
	public static void tearDownAfterClass() {
		subgraphTesterDirected.close();
	}

	@Test
	public void testContainedEdges() {
		for (String edgeName : subgraphTesterDirected.expectedSubgraphEdges) {
			assertNotNull("Edge should be contained in subgraph: " + edgeName, subgraphTesterDirected.getSubgraph()
					.getEdge(FileUtils.lineToLabel(edgeName.split(FileUtils.DELIMITER))));
		}
	}

	@Test
	public void testContainedVertices() {
		for (String vertexName : subgraphTesterDirected.expectedSubgraphVertices) {
			assertNotNull("Vertex should be contained in subgraph: " + vertexName, subgraphTesterDirected.getSubgraph()
					.getVertex(vertexName));
		}
	}

	@Test
	public void testNumberOfEdges() {
		double delta = 0.01;
		assertEquals(subgraphTesterDirected.expectedSubgraphEdges.size(),
				Graphs.edgesCountWeighted(subgraphTesterDirected.getSubgraph(), EDGE_WEIGHTS), delta);
	}

	@Test
	public void testNumberOfVertices() {
		assertEquals(subgraphTesterDirected.expectedSubgraphVertices.size(),
				Graphs.verticesCount(subgraphTesterDirected.getSubgraph()));
	}

}
