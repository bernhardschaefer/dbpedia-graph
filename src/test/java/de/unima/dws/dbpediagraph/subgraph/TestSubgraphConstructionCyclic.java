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

public class TestSubgraphConstructionCyclic {
	private static final int MAX_DISTANCE = 10;
	private static final EdgeWeights EDGE_WEIGHTS = DummyEdgeWeights.INSTANCE;
	
	private static SubgraphTester subGraphData;

	@BeforeClass
	public static void setUpBeforeClass() throws IOException, URISyntaxException {
		subGraphData = new SubgraphTester(TestSet.CYCLIC_FILE_NAMES, new SubgraphConstructionSettings.Builder()
				.maxDistance(MAX_DISTANCE).build());
	}

	@AfterClass
	public static void tearDownAfterClass() {
		subGraphData.close();
	}

	@Test
	public void testContainedEdges() {
		for (String edgeName : subGraphData.expectedSubgraphEdges) {
			assertNotNull("Edge should be contained in subgraph: " + edgeName,
					subGraphData.getSubgraph().getEdge(FileUtils.lineToLabel(edgeName.split(FileUtils.DELIMITER))));
		}
	}

	@Test
	public void testContainedVertices() {
		for (String vertexName : subGraphData.expectedSubgraphVertices) {
			assertNotNull("Vertex should be contained in subgraph: " + vertexName, subGraphData.getSubgraph()
					.getVertex(vertexName));
		}
	}

	@Test
	public void testNumberOfEdges() {
		double delta = 0.01;
		assertEquals(subGraphData.expectedSubgraphEdges.size(),
				Graphs.edgesCountWeighted(subGraphData.getSubgraph(), EDGE_WEIGHTS), delta );
	}

	@Test
	public void testNumberOfVertices() {
		assertEquals(subGraphData.expectedSubgraphVertices.size(), Graphs.verticesCount(subGraphData.getSubgraph()));
	}

}
