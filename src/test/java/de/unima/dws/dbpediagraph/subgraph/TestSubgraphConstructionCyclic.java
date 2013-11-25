package de.unima.dws.dbpediagraph.subgraph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unima.dws.dbpediagraph.graph.*;
import de.unima.dws.dbpediagraph.subgraph.SubgraphConstructionSettings;
import de.unima.dws.dbpediagraph.util.FileUtils;

public class TestSubgraphConstructionCyclic {
	private static final int MAX_DISTANCE = 10;

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
		assertEquals(subGraphData.expectedSubgraphEdges.size(), Graphs.edgesCount(subGraphData.getSubgraph()));
	}

	@Test
	public void testNumberOfVertices() {
		assertEquals(subGraphData.expectedSubgraphVertices.size(), Graphs.verticesCount(subGraphData.getSubgraph()));
	}

}
