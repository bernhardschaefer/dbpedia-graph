package de.unima.dws.dbpediagraph.graphdb.disambiguate.global;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unima.dws.dbpediagraph.graphdb.GlobalDisambiguationTester;
import de.unima.dws.dbpediagraph.graphdb.SubgraphTester;
import de.unima.dws.dbpediagraph.graphdb.model.DefaultSense;
import de.unima.dws.dbpediagraph.graphdb.model.DefaultSurfaceForm;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstructionSettings;

public class TestCompactness {
	private static GlobalDisambiguationTester disambiguationNavigli;
	private static SubgraphTester subgraphNavigli;

	@BeforeClass
	public static void setUp() {
		SubgraphConstructionSettings settings = SubgraphTester.getNavigliSettings();
		subgraphNavigli = SubgraphTester.newNavigliTester(settings);
		disambiguationNavigli = new GlobalDisambiguationTester(new Compactness<DefaultSurfaceForm, DefaultSense>(
				settings), subgraphNavigli);
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
