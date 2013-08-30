package de.unima.dws.dbpediagraph.graphdb;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;

import de.unima.dws.dbpediagraph.graphdb.util.FileUtils;

public class SubgraphTestData {

	/** Test senses from Navigli&Lapata (2010) */
	private static final String NL_SENSES = "/nl-test.senses";

	/** Test vertices from Navigli&Lapata (2010) */
	private static final String NL_VERTICES = "/nl-test.vertices";

	/** Test edges from Navigli&Lapata (2010) */
	private static final String NL_EDGES = "/nl-test.edges";

	protected Graph graph;

	protected Collection<Vertex> allWordsSenses;

	protected List<String> vertices;

	protected List<String> edges;

	public SubgraphTestData() {
		try {
			graph = parseTestGraph();
			allWordsSenses = parseAllWordsSenses(graph);
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
		return allWordsSenses;
	}

	public List<String> getVertices() {
		return vertices;
	}

	private Collection<Vertex> parseAllWordsSenses(Graph graph) throws IOException, URISyntaxException {
		List<Vertex> senses = new ArrayList<>();

		List<String> senseStrings = FileUtils.readLinesFromFile(this.getClass(), NL_SENSES);
		for (String s : senseStrings) {
			Vertex v = graph.getVertex(s);
			senses.add(v);
		}

		return Collections.unmodifiableList(senses);
	}

	private Graph parseTestGraph() throws IOException, URISyntaxException {
		Graph graph = new TinkerGraph();

		vertices = FileUtils.readLinesFromFile(this.getClass(), NL_VERTICES);
		edges = FileUtils.readLinesFromFile(this.getClass(), NL_EDGES);

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
