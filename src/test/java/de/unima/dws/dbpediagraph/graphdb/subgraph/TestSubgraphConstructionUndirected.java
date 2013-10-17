package de.unima.dws.dbpediagraph.graphdb.subgraph;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Graph;

/**
 * Test class for {@link SubgraphConstruction}.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class TestSubgraphConstructionUndirected extends AbstractTestSubgraphConstruction {

	private Graph undirectedSubgraph;

	@Before
	public void setUp() throws Exception {
		SubgraphConstruction scUndirected = SubgraphConstructionFactory.newDefaultImplementation(data.graph,
				new SubgraphConstructionSettings().direction(Direction.BOTH));
		undirectedSubgraph = scUndirected.createSubgraphFromSenses(data.allWordsSenses);
	}

	@After
	public void tearDown() throws Exception {
		super.close();
		if (undirectedSubgraph != null)
			undirectedSubgraph.shutdown();
	}

	@Test
	public void testAllEdgesContainedUndirected() {
		allEdgesContained(undirectedSubgraph);
	}

	@Test
	public void testAllNodesContainedUndirected() {
		allNodesContained(undirectedSubgraph);
	}

	@Test
	public void testSubgraphContainsSensesUndirected() {
		subgraphContainsSenses(undirectedSubgraph);
	}
}
