package de.unima.dws.dbpediagraph.graphdb.disambiguate;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unima.dws.dbpediagraph.graphdb.DisambiguationTestData;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstructionNavigliOld;

public class TestHITSCentrality {
	private static DisambiguationTestData data;

	@BeforeClass
	public static void setUp() {
		data = new DisambiguationTestData(new HITSCentrality(), new SubgraphConstructionNavigliOld());
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
