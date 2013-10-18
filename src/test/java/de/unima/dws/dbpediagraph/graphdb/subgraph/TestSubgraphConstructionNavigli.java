package de.unima.dws.dbpediagraph.graphdb.subgraph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.Graphs;
import de.unima.dws.dbpediagraph.graphdb.SubgraphTester;
import de.unima.dws.dbpediagraph.graphdb.TestSet;

public class TestSubgraphConstructionNavigli {
	private SubgraphTester subGraphData;

	@Before
	public void setUp() {
		subGraphData = new SubgraphTester(TestSet.NAVIGLI_FILE_NAMES, SubgraphConstructionFactory.defaultClass());
	}

	@After
	public void tearDown() {
		if (subGraphData != null)
			subGraphData.close();
	}

	@Test
	public void testAllEdgesContainedDirected() {
		assertEquals(subGraphData.expectedSubgraphEdges.size(), Graphs.getNumberOfEdges(subGraphData.getSubgraph()));
	}

	@Test
	public void testAllNodesContainedDirected() {
		assertEquals(subGraphData.expectedSubgraphVertices.size(),
				Graphs.getNumberOfVertices(subGraphData.getSubgraph()));
	}

	@Test
	public void testSubgraphContainsSensesDirected() {
		for (Vertex s : subGraphData.allSenses) {
			assertNotNull("The sense vertex " + s.getId() + " should be contained in the subgraph.", subGraphData
					.getSubgraph().getVertex(s.getId()));
		}
	}

}
