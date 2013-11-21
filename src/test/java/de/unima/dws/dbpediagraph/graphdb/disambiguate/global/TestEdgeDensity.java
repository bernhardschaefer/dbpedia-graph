package de.unima.dws.dbpediagraph.graphdb.disambiguate.global;

import org.junit.*;

import de.unima.dws.dbpediagraph.graphdb.GlobalDisambiguationTester;
import de.unima.dws.dbpediagraph.graphdb.SubgraphTester;
import de.unima.dws.dbpediagraph.graphdb.model.*;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstructionSettings;

public class TestEdgeDensity {
	private static GlobalDisambiguationTester disambiguationNavigli;
	private static SubgraphTester subgraphTesterNavigli;

	@BeforeClass
	public static void setUp() {
		SubgraphConstructionSettings settings = SubgraphTester.getNavigliSettings();
		subgraphTesterNavigli = SubgraphTester.newNavigliTester(settings);
		disambiguationNavigli = new GlobalDisambiguationTester(new EdgeDensity<DefaultSurfaceForm, DefaultSense>(
				settings, DefaultModelFactory.INSTANCE), subgraphTesterNavigli);
	}

	@AfterClass
	public static void tearDown() {
		if (subgraphTesterNavigli != null)
			subgraphTesterNavigli.close();
	}

	@Test
	public void testDisambiguateValues() {
		disambiguationNavigli.compareDisambiguationResults();
	}
}
