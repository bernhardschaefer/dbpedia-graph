package de.unima.dws.dbpediagraph.graphdb.disambiguate.local;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unima.dws.dbpediagraph.graphdb.DisambiguationTestData;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstructionNavigliOld;

public class TestPageRankCentrality {
	private static DisambiguationTestData data;

	@BeforeClass
	public static void setUp() {
		double alpha = 0.15;
		data = new DisambiguationTestData(new PageRankCentrality(alpha), new SubgraphConstructionNavigliOld());
	}

	@AfterClass
	public static void tearDown() {
		if (data != null)
			data.close();
	}

	@Test
	public void testDisambiguateValues() {
		data.checkWeightedUris();
	}

	@Test
	public void testWeightedUrisSize() {
		assertEquals(data.getWeightedUris().size(), data.getSenses().size());
	}

}
