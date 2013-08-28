package de.unima.dws.dbpediagraph.graphdb.disambiguate;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.tinkerpop.blueprints.Direction;

import de.unima.dws.dbpediagraph.graphdb.DisambiguationTestData;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstructionNavigliOld;

public class TestDegreeCentrality {

	private static DisambiguationTestData data;
	private static DegreeCentrality degreeCentrality;
	private static Direction direction;

	@BeforeClass
	public static void setUp() {
		direction = Direction.BOTH;
		degreeCentrality = new DegreeCentrality(direction);

		data = new DisambiguationTestData(degreeCentrality, new SubgraphConstructionNavigliOld());
	}

	@AfterClass
	public static void tearDown() {
		if (data != null)
			data.close();
	}

	@Test
	public void testDisambiguateValues() {
		data.checkWeightedUris(LocalConnectivityMeasure.Degree);
	}

	@Test
	public void testWeightedUrisSize() {
		assertEquals(data.getWeightedUris().size(), data.getSenses().size());
	}

}
