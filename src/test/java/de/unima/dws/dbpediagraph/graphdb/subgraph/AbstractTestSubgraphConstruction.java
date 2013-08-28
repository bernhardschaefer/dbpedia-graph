package de.unima.dws.dbpediagraph.graphdb.subgraph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;
import java.util.List;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.GraphUtil;
import de.unima.dws.dbpediagraph.graphdb.SubgraphTestData;

public abstract class AbstractTestSubgraphConstruction {

	protected final Graph graph;

	protected final Collection<Vertex> senses;

	protected final List<String> vertices;

	protected final List<String> edges;

	public AbstractTestSubgraphConstruction() {
		SubgraphTestData data = new SubgraphTestData();
		graph = data.getGraph();
		senses = data.getSenses();
		vertices = data.getVertices();
		edges = data.getEdges();

	}

	public void allEdgesContained(Graph g) {
		assertEquals(edges.size(), GraphUtil.getNumberOfEdges(g));
	}

	public void allNodesContained(Graph g) {
		assertEquals(vertices.size(), GraphUtil.getNumberOfVertices(g));
	}

	public void close() {
		if (graph != null)
			graph.shutdown();
	}

	public void subgraphContainsSenses(Graph g) {
		for (Vertex s : senses) {
			assertNotNull("The sense vertex " + s.getId() + " should be contained in the subgraph.",
					g.getVertex(s.getId()));
		}
	}

}
