package de.unima.dws.dbpediagraph.graphdb.disambiguate.local;

import static org.junit.Assert.assertEquals;

import org.junit.*;

import de.unima.dws.dbpediagraph.graphdb.*;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.LocalGraphDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.model.*;

public class TestHITSCentrality {
	private static LocalDisambiguationTester disambiguationNavigli;
	private static SubgraphTester subgraphNavigli;

	@BeforeClass
	public static void setUp() {
		subgraphNavigli = SubgraphTester.newNavigliTester();
		LocalGraphDisambiguator<DefaultSurfaceForm, DefaultSense> localDisambiguator = new HITSCentrality<>(
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
		// TODO enable when hits is working
		// disambiguationNavigli.compareDisambiguationResults();
	}

	@Test
	public void testResultingListSize() {
		assertEquals(subgraphNavigli.senseVertices.size(), disambiguationNavigli.getActualAllScoresResults().size());
	}

}
