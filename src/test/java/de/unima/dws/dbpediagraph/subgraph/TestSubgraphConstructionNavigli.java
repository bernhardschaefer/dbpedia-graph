package de.unima.dws.dbpediagraph.subgraph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.*;

import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graph.Graphs;
import de.unima.dws.dbpediagraph.graph.SubgraphTester;
import de.unima.dws.dbpediagraph.weights.DummyEdgeWeights;
import de.unima.dws.dbpediagraph.weights.EdgeWeights;

/**
 * @author Bernhard Schäfer
 */
public class TestSubgraphConstructionNavigli {
	private static final EdgeWeights EDGE_WEIGHTS = DummyEdgeWeights.INSTANCE;

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
		double delta = 0.01;
		assertEquals(subgraphNavigli.expectedSubgraphEdges.size(),
				Graphs.edgesCountWeighted(subgraphNavigli.getSubgraph(), EDGE_WEIGHTS), delta);
	}

	@Test
	public void testAllNodesContainedDirected() {
		assertEquals(subgraphNavigli.expectedSubgraphVertices.size(),
				Graphs.verticesCount(subgraphNavigli.getSubgraph()));
	}

	@Test
	public void testSubgraphContainsSensesDirected() {
		for (Vertex s : subgraphNavigli.senseVertices) {
			assertNotNull("The sense vertex " + s.getId() + " should be contained in the subgraph.", subgraphNavigli
					.getSubgraph().getVertex(s.getId()));
		}
	}

}
