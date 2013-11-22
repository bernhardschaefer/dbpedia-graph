package de.unima.dws.dbpediagraph.graphdb.disambiguate.global;

import org.junit.*;

import de.unima.dws.dbpediagraph.graphdb.GlobalDisambiguationTester;
import de.unima.dws.dbpediagraph.graphdb.SubgraphTester;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.GlobalGraphDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.model.*;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstructionSettings;

public class TestEdgeDensity {
	private static GlobalDisambiguationTester disambiguationNavigli;
	private static SubgraphTester subgraphTesterNavigli;
	private static GlobalGraphDisambiguator<DefaultSurfaceForm, DefaultSense> disambiguator;

	@BeforeClass
	public static void setUp() {
		SubgraphConstructionSettings settings = SubgraphTester.getNavigliSettings();
		subgraphTesterNavigli = SubgraphTester.newNavigliTester(settings);
		disambiguator = new EdgeDensity<DefaultSurfaceForm, DefaultSense>(settings, DefaultModelFactory.INSTANCE);
		disambiguationNavigli = new GlobalDisambiguationTester(disambiguator, subgraphTesterNavigli);
	}

	@AfterClass
	public static void tearDown() {
		if (subgraphTesterNavigli != null)
			subgraphTesterNavigli.close();
	}

	@Test
	public void testDisambiguateValues() {
		disambiguationNavigli.compareAllDisambiguationResults();
	}

	@Test
	public void testDisambiguation() {
//		List<SurfaceFormSenseScore<DefaultSurfaceForm, DefaultSense>> actualDisambiguationResults = disambiguator
//				.disambiguate(subgraphTesterNavigli.surfaceFormSenses, subgraphTesterNavigli.getSubgraph());
//		TestDisambiguationHelper.compareDisambiguatedAssignment(
//				disambiguationNavigli.getExpectedDisambiguationResults(), actualDisambiguationResults,
//				Compactness.class);
	}
}
