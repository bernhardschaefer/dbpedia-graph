package de.unima.dws.dbpediagraph.graphdb;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

public class GraphHelper {
	private static final Logger logger = LoggerFactory.getLogger(GraphHelper.class);

	public static Collection<Vertex> getTestVertices(Graph graph) {
		// http://en.wikipedia.org/wiki/Michael_I._Jordan
		// Michael I. Jordan is a leading researcher in machine learning and
		// artificial intelligence.

		String[] resources = new String[] { "Michael_I._Jordan", "Michael_Jordan", "Machine_learning",
				"Artificial_intelligence" };

		Collection<Vertex> vertices = new LinkedList<Vertex>();
		for (String resource : resources) {
			String uri = GraphConfig.DBPEDIA_RESOURCE_URI + resource;
			vertices.add(GraphHelper.getVertexByUri(graph, uri));
		}

		return vertices;
	}

	public static Vertex getVertexByUri(Graph graph, String uri) {
		String shortUri = UriShortener.shorten(uri);
		List<Vertex> vertices = new LinkedList<Vertex>();
		Iterable<Vertex> verticesIter = graph.getVertices(GraphConfig.URI_PROPERTY, shortUri);
		for (Vertex v : verticesIter) {
			vertices.add(v);
		}

		if (vertices.size() == 0) {
			return null;
		}
		if (vertices.size() > 1) {
			logger.warn("There is more than one vertex with the uri " + uri);
		}

		return vertices.get(0);
	}

	public static String getVertexToString(Vertex v) {
		String uri = v.getProperty(GraphConfig.URI_PROPERTY);
		return String.format("vid: %s uri: %s", v.getId().toString(), uri);
	}

	public static List<Vertex> getVerticesByUri(Graph graph, List<String> uris) {
		List<Vertex> vertices = new LinkedList<Vertex>();
		for (String uri : uris) {
			Vertex v = getVertexByUri(graph, uri);
			if (v != null) {
				vertices.add(v);
			}
		}
		return vertices;
	}

}
