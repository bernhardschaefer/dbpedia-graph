package de.unima.dws.dbpediagraph.demo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graph.GraphFactory;
import de.unima.dws.dbpediagraph.graph.Graphs;

public class VertexTest {

	private static final String[] dumpFullUris = new String[] { "http://dbpedia.org/resource/Company",
			"http://dbpedia.org/resource/Oil", //
			"http://dbpedia.org/resource/Company_%28military_unit%29", // Company_(military_unit)
			"http://dbpedia.org/resource/War_in_Afghanistan_(2001%E2%80%93present)" //War_in_Afghanistan_(2001–present)
			};

	public static void main(String[] args) throws IOException, URISyntaxException {
		// checkInDBpediaGraph(fullUris);
		for (String fullUri : dumpFullUris) {
			String decoded = URLDecoder.decode(fullUri, "UTF-8");
			System.out.println(decoded);
		}

	}

	public static void checkInDBpediaGraph(String[] fullUris) throws UnsupportedEncodingException {
		Graph graph = GraphFactory.getDBpediaGraph();
		for (String fullUri : fullUris) {
			Vertex v = Graphs.vertexByFullUri(graph, URLDecoder.decode(fullUri, "UTF-8"));
			String id = (v == null) ? "null" : v.getId().toString();
			System.out.println(fullUri + " --> " + id);
		}

		graph.shutdown();
	}
}
