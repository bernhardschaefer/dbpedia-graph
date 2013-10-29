package de.unima.dws.dbpediagraph.graphdb.disambiguate.local;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.Test;

import de.unima.dws.dbpediagraph.graphdb.GraphType;
import de.unima.dws.dbpediagraph.graphdb.LocalDisambiguationTester;
import de.unima.dws.dbpediagraph.graphdb.SubgraphTester;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.LocalGraphDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.model.DefaultModelFactory;
import de.unima.dws.dbpediagraph.graphdb.model.DefaultSense;
import de.unima.dws.dbpediagraph.graphdb.model.DefaultSurfaceForm;

public class TestBetweennessCentrality {
	private static final LocalDisambiguationTester disambiguationNavigli;
	private static final SubgraphTester subgraphNavigli;
	static {
		subgraphNavigli = SubgraphTester.newNavigliTester();
		LocalGraphDisambiguator<DefaultSurfaceForm, DefaultSense> localDisambiguator = new BetweennessCentrality<>(
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
	public void testResultingListSize() {
		assertEquals(subgraphNavigli.allSenses.size(), disambiguationNavigli.getActualAllScoresResults().size());
	}
}
