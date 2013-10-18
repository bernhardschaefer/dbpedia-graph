package de.unima.dws.dbpediagraph.graphdb.subgraph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unima.dws.dbpediagraph.graphdb.GraphUtil;
import de.unima.dws.dbpediagraph.graphdb.SubgraphTester;
import de.unima.dws.dbpediagraph.graphdb.TestSet;
import de.unima.dws.dbpediagraph.graphdb.util.FileUtils;

public class TestSubgraphConstructionCyclic {
	private static final int MAX_DISTANCE = 10;

	private static SubgraphTester subGraphData;

	@BeforeClass
	public static void setUpBeforeClass() throws IOException, URISyntaxException {
		subGraphData = new SubgraphTester(TestSet.CYCLIC_FILE_NAMES, SubgraphConstructionFactory.defaultClass(),
				new SubgraphConstructionSettings().maxDistance(MAX_DISTANCE));
	}

	@AfterClass
	public static void tearDownAfterClass() {
		subGraphData.close();
	}

	@Test
	public void testContainedEdges() {
		for (String edgeName : subGraphData.expectedSubgraphEdges) {
			assertNotNull("Edge should be contained in subgraph: " + edgeName,
					subGraphData.getSubgraph().getEdge(FileUtils.lineToLabel(edgeName)));
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
		assertEquals(subGraphData.expectedSubgraphEdges.size(), GraphUtil.getNumberOfEdges(subGraphData.getSubgraph()));
	}

	@Test
	public void testNumberOfVertices() {
		assertEquals(subGraphData.expectedSubgraphVertices.size(),
				GraphUtil.getNumberOfVertices(subGraphData.getSubgraph()));
	}

}
