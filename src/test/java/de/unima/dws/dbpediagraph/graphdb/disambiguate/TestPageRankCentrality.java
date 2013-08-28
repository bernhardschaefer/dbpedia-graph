package de.unima.dws.dbpediagraph.graphdb.disambiguate;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.unima.dws.dbpediagraph.graphdb.DisambiguationTestData;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstructionNavigliOld;

public class TestPageRankCentrality {
	private static final double DELTA = 0.001;
	private DisambiguationTestData data;

	@Before
	public void setUp() {
		double alpha = 0.15;
		Disambiguator disambiguator = new PageRankCentrality(alpha);

		data = new DisambiguationTestData(disambiguator, new SubgraphConstructionNavigliOld());

	}

	@After
	public void tearDown() {
		data.close();
	}

	@Test
	public void testDisambiguate() {
		assertEquals(data.getWeightedUris().size(), data.getSenses().size());

		for (WeightedUri wUri : data.getWeightedUris()) {
			// float degree = Lists.newArrayList(GraphUtil.getVertexByUri(subgraph, wUri.getUri()).getEdges(direction))
			// .size();
			// float v = data.getVertices().size();
			// assertEquals(degree / (v - 1), wUri.getWeight(), DELTA);
			System.out.println(String.format("uri: %s weight: %f", wUri.getUri(), wUri.getWeight()));
		}
	}

}
