package de.unima.dws.dbpediagraph.demo;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLDecoder;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graph.GraphFactory;
import de.unima.dws.dbpediagraph.graph.Graphs;

public class VertexTest {

	private static final String[] uris = new String[] { "http://dbpedia.org/resource/Company",
			"http://dbpedia.org/resource/Oil", "http://dbpedia.org/resource/Company_(military_unit)",
			"http://dbpedia.org/resource/Company_%28military_unit%29" };

	public static void main(String[] args) throws IOException, URISyntaxException {
		Graph graph = GraphFactory.getDBpediaGraph();
		for (String uri : uris) {
			Vertex v = Graphs.vertexByUri(graph, URLDecoder.decode( uri, "UTF-8" ));
			String id = (v == null) ? "null" : v.getId().toString();
			System.out.println(uri + " --> " + id);
		}
		
		graph.shutdown();
	}
}
