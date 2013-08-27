package de.unima.dws.dbpediagraph.graphdb;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tinkerpop.blueprints.Graph;

import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstruction;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstructionNavigli;

public class TestSubgraphConstructionDirected extends AbstractTestSubgraphConstruction {
	private Graph directedSubgraph;

	@Before
	public void setUp() {
		SubgraphConstruction scDirected = new SubgraphConstructionNavigli(graph);
		directedSubgraph = scDirected.createSubgraph(senses);
	}

	@After
	public void tearDown() {
		super.close();
		if (directedSubgraph != null)
			directedSubgraph.shutdown();
	}

	@Test
	public void testAllEdgesContainedDirected() {
		allEdgesContained(directedSubgraph);
	}

	@Test
	public void testAllNodesContainedDirected() {
		allNodesContained(directedSubgraph);
	}

	@Test
	public void testSubgraphContainsSensesDirected() {
		subgraphContainsSenses(directedSubgraph);
	}

}
