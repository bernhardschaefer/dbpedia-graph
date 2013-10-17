package de.unima.dws.dbpediagraph.graphdb.subgraph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.GraphUtil;
import de.unima.dws.dbpediagraph.graphdb.SubgraphTestData;

public abstract class AbstractTestSubgraphConstruction {

	protected final SubgraphTestData data;

	public AbstractTestSubgraphConstruction() {
		data = new SubgraphTestData();

	}

	public void allEdgesContained(Graph g) {
		assertEquals(data.edges.size(), GraphUtil.getNumberOfEdges(g));
	}

	public void allNodesContained(Graph g) {
		assertEquals(data.vertices.size(), GraphUtil.getNumberOfVertices(g));
	}

	public void close() {
		if (data.graph != null)
			data.graph.shutdown();
	}

	public void subgraphContainsSenses(Graph g) {
		for (Vertex s : data.allSenses) {
			assertNotNull("The sense vertex " + s.getId() + " should be contained in the subgraph.",
					g.getVertex(s.getId()));
		}
	}

}
