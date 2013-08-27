package de.unima.dws.dbpediagraph.graphdb;

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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;

import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstruction;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstructionNavigli;

/**
 * Test class for {@link SubgraphConstructionNavigli}.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class TestSubgraphConstructionNavigli {

	private Graph graph;

	private Collection<Vertex> senses;

	private Graph subgraph;

	private List<String> vertices;
	private List<String> edges;

	private List<String> readLinesFromTestFile(String fileName) throws IOException, URISyntaxException {
		URI uri = TestSubgraphConstructionNavigli.class.getResource(fileName).toURI();
		return Files.readAllLines(Paths.get(uri), StandardCharsets.UTF_8);
	}

	@Before
	public void setUp() throws Exception {
		graph = setUpGraph();
		senses = setUpSenses(graph);
		SubgraphConstruction sc = new SubgraphConstructionNavigli(graph);
		subgraph = sc.createSubgraph(senses);
		// for (Vertex v : subgraph.getVertices()) {
		// System.out.println(v);
		// }
		// for (Edge e : subgraph.getEdges()) {
		// System.out.println(e);
		// }
	}

	private Graph setUpGraph() throws IOException, URISyntaxException {
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

	private Collection<Vertex> setUpSenses(Graph graph) throws IOException, URISyntaxException {
		List<Vertex> senses = new ArrayList<>();

		List<String> senseStrings = readLinesFromTestFile("/test.senses");
		for (String s : senseStrings) {
			Vertex v = graph.getVertex(s);
			senses.add(v);
		}

		return Collections.unmodifiableList(senses);
	}

	@After
	public void tearDown() throws Exception {
		if (graph != null)
			graph.shutdown();
	}

	@Test
	public void testAllEdgesContained() {
		assertEquals(edges.size(), GraphUtil.getNumberOfEdges(subgraph));
	}

	@Test
	public void testAllNodesContained() {
		assertEquals(vertices.size(), GraphUtil.getNumberOfVertices(subgraph));
	}

	@Test
	public void testSubgraphContainsSenses() {
		for (Vertex s : senses) {
			assertNotNull("The sense vertex " + s.getId() + " should be contained in the subgraph.",
					graph.getVertex(s.getId()));
		}
	}
}
