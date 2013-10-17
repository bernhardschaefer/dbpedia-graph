package de.unima.dws.dbpediagraph.graphdb.disambiguate.local;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.tinkerpop.blueprints.Direction;

import de.unima.dws.dbpediagraph.graphdb.LocalDisambiguationTestData;

public class TestDegreeCentrality {
	private static LocalDisambiguationTestData data;

	@BeforeClass
	public static void setUp() {
		Direction direction = Direction.BOTH;
		data = new LocalDisambiguationTestData(new DegreeCentrality(direction));
	}

	@AfterClass
	public static void tearDown() {
		if (data != null)
			data.close();
	}

	@Test
	public void testDisambiguateValues() {
		data.checkDisambiguationResults();
	}

	@Test
	public void testWeightedUrisSize() {
		assertEquals(data.getWeightedUris().size(), data.getTestData().allSenses.size());
	}

}
