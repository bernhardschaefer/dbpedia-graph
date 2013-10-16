package de.unima.dws.dbpediagraph.graphdb.subgraph;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Graph;

import de.unima.dws.dbpediagraph.graphdb.filter.DummyEdgeFilter;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstruction;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstructionNavigli;

/**
 * Test class for {@link SubgraphConstructionNavigli}.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class TestSubgraphConstructionUndirected extends AbstractTestSubgraphConstruction {

	private Graph undirectedSubgraph;

	@Before
	public void setUp() throws Exception {
		SubgraphConstruction scUndirected = new SubgraphConstructionNavigli(graph, 5, new DummyEdgeFilter(),
				Direction.BOTH);
		undirectedSubgraph = scUndirected.createSubgraph(senses);
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
