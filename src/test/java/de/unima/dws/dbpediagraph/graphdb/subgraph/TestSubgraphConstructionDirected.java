package de.unima.dws.dbpediagraph.graphdb.subgraph;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tinkerpop.blueprints.Graph;

public class TestSubgraphConstructionDirected extends AbstractTestSubgraphConstruction {
	private Graph directedSubgraph;

	@Before
	public void setUp() {
		SubgraphConstruction scDirected = SubgraphConstructionFactory.newDefaultImplementation(data.graph,
				SubgraphConstructionSettings.getDefault());
		directedSubgraph = scDirected.createSubgraphFromSenses(data.allWordsSenses);
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
