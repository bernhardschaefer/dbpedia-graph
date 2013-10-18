package de.unima.dws.dbpediagraph.graphdb.disambiguate.local;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.tinkerpop.blueprints.Direction;

import de.unima.dws.dbpediagraph.graphdb.LocalDisambiguationTestData;
import de.unima.dws.dbpediagraph.graphdb.SubgraphTester;
import de.unima.dws.dbpediagraph.graphdb.TestSet;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstructionFactory;

public class TestDegreeCentrality {
	private static LocalDisambiguationTestData data;
	private static SubgraphTester subGraphData;

	@BeforeClass
	public static void setUp() {
		Direction direction = Direction.BOTH;

		subGraphData = new SubgraphTester(TestSet.NAVIGLI_FILE_NAMES, SubgraphConstructionFactory.defaultClass());
		data = new LocalDisambiguationTestData(new DegreeCentrality(direction), subGraphData);
	}

	@AfterClass
	public static void tearDown() {
		if (subGraphData != null)
			subGraphData.close();
	}

	@Test
	public void testDisambiguateValues() {
		data.checkDisambiguationResults();
	}

	@Test
	public void testWeightedUrisSize() {
		assertEquals(data.getWeightedUris().size(), subGraphData.allSenses.size());
	}

}
