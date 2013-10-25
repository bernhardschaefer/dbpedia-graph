package de.unima.dws.dbpediagraph.graphdb.disambiguate.global;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unima.dws.dbpediagraph.graphdb.GlobalDisambiguationTester;
import de.unima.dws.dbpediagraph.graphdb.SubgraphTester;
import de.unima.dws.dbpediagraph.graphdb.TestSet;
import de.unima.dws.dbpediagraph.graphdb.model.DefaultSense;
import de.unima.dws.dbpediagraph.graphdb.model.DefaultSurfaceForm;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstructionFactory;

public class TestGraphEntropy {
	private static GlobalDisambiguationTester disambiguationNavigli;
	private static SubgraphTester subgraphNavigli;

	@BeforeClass
	public static void setUp() {
		subgraphNavigli = new SubgraphTester(TestSet.NAVIGLI_FILE_NAMES, SubgraphConstructionFactory.defaultClass());
		disambiguationNavigli = new GlobalDisambiguationTester(new GraphEntropy<DefaultSurfaceForm, DefaultSense>(),
				subgraphNavigli);
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
