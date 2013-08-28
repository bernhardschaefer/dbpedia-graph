package de.unima.dws.dbpediagraph.graphdb.subgraph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;

import de.unima.dws.dbpediagraph.graphdb.GraphConfig;
import de.unima.dws.dbpediagraph.graphdb.GraphUtil;

public class AbstractTestSubgraphConstruction {

	protected Graph graph;

	protected Collection<Vertex> senses;

	protected List<String> vertices;

	protected List<String> edges;

	public AbstractTestSubgraphConstruction() {
		try {
			graph = setUpTestGraph();
			senses = setUpSenses(graph);
		} catch (IOException | URISyntaxException e) {
			throw new RuntimeException("Error while trying to construct test graph.", e);
		}

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

	protected List<String> readLinesFromTestFile(String fileName) throws IOException, URISyntaxException {
		URI uri = TestSubgraphConstructionUndirected.class.getResource(fileName).toURI();
		return Files.readAllLines(Paths.get(uri), StandardCharsets.UTF_8);
	}

	protected Collection<Vertex> setUpSenses(Graph graph) throws IOException, URISyntaxException {
		List<Vertex> senses = new ArrayList<>();

		List<String> senseStrings = readLinesFromTestFile("/test.senses");
		for (String s : senseStrings) {
			Vertex v = graph.getVertex(s);
			senses.add(v);
		}

		return Collections.unmodifiableList(senses);
	}

	protected Graph setUpTestGraph() throws IOException, URISyntaxException {
		Graph graph = new TinkerGraph();

		vertices = readLinesFromTestFile("/test.vertices");
		edges = readLinesFromTestFile("/test.edges");

		for (String v : vertices) {
			Vertex vertex = graph.addVertex(v);
			vertex.setProperty(GraphConfig.URI_PROPERTY, v);
		}

		for (String line : edges) {
			String[] srcDest = line.split("\\s+");
			Vertex outVertex = graph.getVertex(srcDest[0]);
			Vertex inVertex = graph.getVertex(srcDest[1]);
			String label = line.replaceAll(" ", "");
			Edge e = graph.addEdge(label, outVertex, inVertex, label);
			e.setProperty(GraphConfig.URI_PROPERTY, label);
		}
		return graph;
	}

	public void subgraphContainsSenses(Graph g) {
		for (Vertex s : senses) {
			assertNotNull("The sense vertex " + s.getId() + " should be contained in the subgraph.",
					g.getVertex(s.getId()));
		}
	}

}
