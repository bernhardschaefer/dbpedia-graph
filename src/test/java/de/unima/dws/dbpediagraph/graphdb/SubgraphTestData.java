package de.unima.dws.dbpediagraph.graphdb;

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

import de.unima.dws.dbpediagraph.graphdb.subgraph.TestSubgraphConstructionUndirected;

public class SubgraphTestData {

	protected Graph graph;

	protected Collection<Vertex> senses;

	protected List<String> vertices;

	protected List<String> edges;

	public SubgraphTestData() {
		try {
			graph = setUpTestGraph();
			senses = setUpSenses(graph);
		} catch (IOException | URISyntaxException e) {
			throw new RuntimeException("Error while trying to construct test graph.", e);
		}
	}

	public void close() {
		if (graph != null)
			graph.shutdown();
	}

	public List<String> getEdges() {
		return edges;
	}

	public Graph getGraph() {
		return graph;
	}

	public Collection<Vertex> getSenses() {
		return senses;
	}

	public List<String> getVertices() {
		return vertices;
	}

	private List<String> readLinesFromTestFile(String fileName) throws IOException, URISyntaxException {
		URI uri = TestSubgraphConstructionUndirected.class.getResource(fileName).toURI();
		return Files.readAllLines(Paths.get(uri), StandardCharsets.UTF_8);
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

	private Graph setUpTestGraph() throws IOException, URISyntaxException {
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

}
