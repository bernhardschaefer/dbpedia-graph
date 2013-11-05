package de.unima.dws.dbpediagraph.graphdb.subgraph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.Graphs;
import de.unima.dws.dbpediagraph.graphdb.SubgraphTester;

public class TestSubgraphConstructionNavigli {
	private SubgraphTester subgraphNavigli;

	@Before
	public void setUp() {
		subgraphNavigli = SubgraphTester.newNavigliTester();
	}

	@After
	public void tearDown() {
		if (subgraphNavigli != null)
			subgraphNavigli.close();
	}

	@Test
	public void testAllEdgesContainedDirected() {
		assertEquals(subgraphNavigli.expectedSubgraphEdges.size(), Graphs.edgesCount(subgraphNavigli.getSubgraph()));
	}

	@Test
	public void testAllNodesContainedDirected() {
		assertEquals(subgraphNavigli.expectedSubgraphVertices.size(),
				Graphs.verticesCount(subgraphNavigli.getSubgraph()));
	}

	@Test
	public void testSubgraphContainsSensesDirected() {
		for (Vertex s : subgraphNavigli.allSenses) {
			assertNotNull("The sense vertex " + s.getId() + " should be contained in the subgraph.", subgraphNavigli
					.getSubgraph().getVertex(s.getId()));
		}
	}

}
