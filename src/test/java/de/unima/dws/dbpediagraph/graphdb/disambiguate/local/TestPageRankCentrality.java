package de.unima.dws.dbpediagraph.graphdb.disambiguate.local;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unima.dws.dbpediagraph.graphdb.GraphType;
import de.unima.dws.dbpediagraph.graphdb.LocalDisambiguationTester;
import de.unima.dws.dbpediagraph.graphdb.SubgraphTester;
import de.unima.dws.dbpediagraph.graphdb.TestSet;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstructionFactory;

public class TestPageRankCentrality {
	private static LocalDisambiguationTester disambiguationTester;
	private static SubgraphTester navigliSubgraphData;

	@BeforeClass
	public static void setUp() {
		navigliSubgraphData = new SubgraphTester(TestSet.NAVIGLI_FILE_NAMES, SubgraphConstructionFactory.defaultClass());
		GraphType graphType = GraphType.UNDIRECTED_GRAPH;
		disambiguationTester = new LocalDisambiguationTester(PageRankCentrality.defaultForGraphType(graphType), navigliSubgraphData);
	}

	@AfterClass
	public static void tearDown() {
		if (navigliSubgraphData != null)
			navigliSubgraphData.close();
	}

	@Test
	public void testDisambiguateValues() {
		disambiguationTester.compareDisambiguationResults();
	}

	@Test
	public void testWeightedUrisSize() {
		assertEquals(disambiguationTester.getActualDisambiguationResults().size(), navigliSubgraphData.allSenses.size());
	}

}
