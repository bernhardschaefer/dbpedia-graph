package de.unima.dws.dbpediagraph.graphdb.disambiguate.local;

import static org.junit.Assert.assertEquals;

import org.junit.*;

import de.unima.dws.dbpediagraph.graphdb.*;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.GraphDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.model.*;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstructionFactory;

public class TestKPPCentrality {
	private static LocalDisambiguationTester disambiguationNavigli;
	private static SubgraphTester subgraphNavigli;

	@BeforeClass
	public static void setUp() {
		subgraphNavigli = new SubgraphTester(TestSet.NAVIGLI_FILE_NAMES, SubgraphConstructionFactory.defaultClass());
		GraphDisambiguator<DefaultSurfaceForm, DefaultSense> localDisambiguator = new KPPCentrality<>(
				GraphType.UNDIRECTED_GRAPH, DefaultModelFactory.INSTANCE);
		disambiguationNavigli = new LocalDisambiguationTester(localDisambiguator, subgraphNavigli);
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

	@Test
	public void testWeightedUrisSize() {
		assertEquals(disambiguationNavigli.getActualDisambiguationResults().size(), subgraphNavigli.allSenses.size());
	}
}
