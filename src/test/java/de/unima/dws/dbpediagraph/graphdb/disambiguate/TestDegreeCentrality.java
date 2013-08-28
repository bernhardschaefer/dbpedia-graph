package de.unima.dws.dbpediagraph.graphdb.disambiguate;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.tinkerpop.blueprints.Direction;

import de.unima.dws.dbpediagraph.graphdb.DisambiguationTestData;
import de.unima.dws.dbpediagraph.graphdb.GraphUtil;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstructionNavigliOld;

public class TestDegreeCentrality {

	private static final double DELTA = 0.001;
	private DisambiguationTestData data;
	private DegreeCentrality degreeCentrality;
	private Direction direction;

	@Before
	public void setUp() {
		direction = Direction.BOTH;
		degreeCentrality = new DegreeCentrality(direction);

		data = new DisambiguationTestData(degreeCentrality, new SubgraphConstructionNavigliOld());
	}

	@After
	public void tearDown() {
		data.close();
	}

	@Test
	public void testDisambiguate() {
		assertEquals(data.getWeightedUris().size(), data.getSenses().size());

		for (WeightedUri wUri : data.getWeightedUris()) {
			float degree = Lists.newArrayList(
					GraphUtil.getVertexByUri(data.getSubgraph(), wUri.getUri()).getEdges(direction)).size();
			float v = data.getVertices().size();
			assertEquals(degree / (v - 1), wUri.getWeight(), DELTA);
			// System.out.println(String.format("uri: %s weight: %f", wUri.getUri(), wUri.getWeight()));
		}
	}

}
