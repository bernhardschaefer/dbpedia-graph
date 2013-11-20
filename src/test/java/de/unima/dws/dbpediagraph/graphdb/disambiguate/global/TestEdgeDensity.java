package de.unima.dws.dbpediagraph.graphdb.disambiguate.global;

import org.junit.*;

import de.unima.dws.dbpediagraph.graphdb.GlobalDisambiguationTester;
import de.unima.dws.dbpediagraph.graphdb.SubgraphTester;
import de.unima.dws.dbpediagraph.graphdb.model.*;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstructionSettings;

public class TestEdgeDensity {
	private static GlobalDisambiguationTester disambiguationNavigli;
	private static SubgraphTester subgraphNavigli;

	@BeforeClass
	public static void setUp() {
		SubgraphConstructionSettings settings = SubgraphTester.getNavigliSettings();
		subgraphNavigli = SubgraphTester.newNavigliTester(settings);
		disambiguationNavigli = new GlobalDisambiguationTester(new EdgeDensity<DefaultSurfaceForm, DefaultSense>(
				settings, DefaultModelFactory.INSTANCE), subgraphNavigli);
	}

	@AfterClass
	public static void tearDown() {
		if (subgraphNavigli != null)
			subgraphNavigli.close();
	}

	@Test
	public void testDisambiguateValues() {
		disambiguationNavigli.compareDisambiguationResults();
	}
}
