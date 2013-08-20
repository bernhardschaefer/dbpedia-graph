package de.unima.dws.dbpediagraph.graphdb.algorithms;

import java.util.Iterator;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.java.GremlinPipeline;

import de.unima.dws.dbpediagraph.graphdb.GraphConfig;
import de.unima.dws.dbpediagraph.graphdb.UriShortener;
import de.unima.dws.dbpediagraph.graphdb.util.Timer;

public class GraphProcessor {
	public static void main(String[] args) throws ConfigurationException {
		GraphProcessor processor = new GraphProcessor();
		processor.stats();

		// processor.test1();
		// processor.test2();

		processor.graph.shutdown();
	}

	private final Logger logger = LoggerFactory.getLogger(GraphProcessor.class);

	private final TransactionalGraph graph;

	public GraphProcessor() {
		graph = GraphConfig.getInstance().getGraph();
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
			Set<String> keys = v.getPropertyKeys();
			System.out.println("vid: " + v.getId().toString());
			for (String key : keys) {
				logger.info(String.format("key: %s, val: %s %n", key, v.getProperty(key)));
			}
		}
	}

	public void stats() {
		Timer t = new Timer();

		long verticesCount = new GremlinPipeline<Object, Object>(graph.getVertices()).count();
		logger.info(String.format("Vertices: %,d %n", verticesCount));
		t.tick(" count vertices ");

		long edgesCount = new GremlinPipeline<Object, Object>(graph.getEdges()).count();
		logger.info(String.format("Edges: %,d %n", edgesCount));
		t.tick(" count edges ");

		t.getTime(" total stats ");
	}

	public void test1() {
		Iterator<Vertex> vertices = graph.getVertices().iterator();
		printVertices(vertices);
	}

	public void test2() {
		long startTime = System.currentTimeMillis();
		Iterator<Vertex> vertices = graph.getVertices(GraphConfig.URI_PROPERTY,
				UriShortener.shorten("http://dbpedia.org/resource/Audi")).iterator();
		printEdgesOfVertices(vertices);
		// GremlinPipeline pipe = new GremlinPipeline();
		// pipe.start(graph.getVertex(1)).out("knows").property("name");

		logger.info(String.format("Total time: %.2f sec %n", (System.currentTimeMillis() - startTime) / 1000.0));
	}

}
