package de.unima.dws.dbpediagraph.graphdb.algorithms;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.java.GremlinPipeline;

import de.unima.dws.dbpediagraph.graphdb.GraphConfig;
import de.unima.dws.dbpediagraph.graphdb.GraphProvider;
import de.unima.dws.dbpediagraph.graphdb.UriShortener;
import de.unima.dws.dbpediagraph.graphdb.util.Timer;

public class GraphProcessor {
	public static void main(String[] args) throws ConfigurationException {
		GraphProcessor processor = new GraphProcessor();
		// processor.stats();

		processor.disambiguateTest();
		// processor.getVertexTest("http://dbpedia.org/resource/Abraham_Lincoln");

		processor.graph.shutdown();
	}

	private static final Logger logger = LoggerFactory.getLogger(GraphProcessor.class);

	private final TransactionalGraph graph;

	public GraphProcessor() {
		graph = GraphProvider.getInstance().getGraph();
	}

	public void printEdgesOfVertices(Iterator<Vertex> vertices) {
		while (vertices.hasNext()) {
			Vertex v = vertices.next();
			for (Edge e : v.getEdges(Direction.OUT)) {
				logger.info(v.getProperty(GraphConfig.URI_PROPERTY).toString());
				logger.info(e.getProperty(GraphConfig.URI_PROPERTY).toString());
				logger.info(e.getVertex(Direction.IN).getProperty(GraphConfig.URI_PROPERTY).toString());
			}
		}
	}

	public void printVertices(Iterator<Vertex> vertices) {
		printVertices(vertices, 100);
	}

	public void printVertices(Iterator<Vertex> vertices, int maxCount) {
		while (maxCount-- > 0 && vertices.hasNext()) {
			Vertex v = vertices.next();
			printVertex(v);
		}
	}

	private void printVertex(Vertex v) {
		String uri = v.getProperty(GraphConfig.URI_PROPERTY);
		logger.info("vid: {} uri: {}", v.getId().toString(), uri);
	}

	public void stats() {
		Timer t = new Timer();

		long verticesCount = new GremlinPipeline<Object, Object>(graph.getVertices()).count();
		logger.info(String.format("Vertices: %,d", verticesCount));
		t.tick(" count vertices ");

		long edgesCount = new GremlinPipeline<Object, Object>(graph.getEdges()).count();
		logger.info(String.format("Edges: %,d", edgesCount));
		t.tick(" count edges ");

		t.getTime(" total stats ");
	}

	public void disambiguateTest() {
		// http://en.wikipedia.org/wiki/Michael_I._Jordan

		// Michael I. Jordan is a leading researcher in machine learning and
		// artificial intelligence.

		String[] resources = new String[] { "Michael_I._Jordan", "Michael_Jordan", "Machine_learning",
				"Artificial_intelligence" };

		Collection<Vertex> vertices = new LinkedList<Vertex>();
		for (String resource : resources) {
			String uri = GraphConfig.DBPEDIA_RESOURCE_URI + resource;
			vertices.add(getVertexByUri(graph, uri));
		}

		printVertices(vertices.iterator());
	}

	private static Vertex getVertexByUri(Graph graph, String uri) {
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

	public void getVertexTest(String uri) {
		long startTime = System.currentTimeMillis();

		Vertex v = getVertexByUri(graph, uri);
		printVertex(v);
		// GremlinPipeline pipe = new GremlinPipeline();
		// pipe.start(graph.getVertex(1)).out("knows").property("name");

		logger.info("Total time: %.2f sec", (System.currentTimeMillis() - startTime) / 1000.0);
	}

}
