package de.unima.dws.dbpediagraph.graphdb;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;

import de.unima.dws.dbpediagraph.graphdb.util.CollectionUtils;
import de.unima.dws.dbpediagraph.graphdb.util.FileUtils;

public class SubgraphTestData {

	/** Test senses from Navigli&Lapata (2010) */
	private static final String NL_SENSES = "/nl-test.senses";

	/** Test vertices from Navigli&Lapata (2010) */
	private static final String NL_VERTICES = "/nl-test.vertices";

	/** Test edges from Navigli&Lapata (2010) */
	private static final String NL_EDGES = "/nl-test.edges";

	public Graph graph;

	public Collection<Collection<Vertex>> allWordsSenses;
	public Collection<Vertex> allSenses;

	public List<String> vertices;

	public List<String> edges;

	public SubgraphTestData() {
		try {
			graph = parseTestGraph();
			allWordsSenses = FileUtils.parseAllWordsSenses(graph, NL_SENSES, getClass(), "");
			allSenses = CollectionUtils.combine(allWordsSenses);
		} catch (IOException | URISyntaxException e) {
			throw new RuntimeException("Error while trying to construct test graph.", e);
		}
	}

	public void close() {
		if (graph != null)
			graph.shutdown();
	}

	private Graph parseTestGraph() throws IOException, URISyntaxException {
		Graph graph = new TinkerGraph();

		vertices = FileUtils.readRelevantLinesFromFile(this.getClass(), NL_VERTICES);
		edges = FileUtils.readRelevantLinesFromFile(this.getClass(), NL_EDGES);

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
