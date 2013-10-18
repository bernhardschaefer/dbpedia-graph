package de.unima.dws.dbpediagraph.graphdb.disambiguate.global;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unima.dws.dbpediagraph.graphdb.GlobalDisambiguationTester;
import de.unima.dws.dbpediagraph.graphdb.SubgraphTester;
import de.unima.dws.dbpediagraph.graphdb.TestSet;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstructionFactory;

public class TestGraphEntropy {
	private static GlobalDisambiguationTester data;
	private static SubgraphTester subGraphData;

	@BeforeClass
	public static void setUp() {
		subGraphData = new SubgraphTester(TestSet.NAVIGLI_FILE_NAMES, SubgraphConstructionFactory.defaultClass());
		data = new GlobalDisambiguationTester(new GraphEntropy(), subGraphData);
	}

	@AfterClass
	public static void tearDown() {
		if (subGraphData != null)
			subGraphData.close();
	}

	@Test
	public void testDisambiguateValues() {
		data.compareDisambiguationResults();
	}

}
