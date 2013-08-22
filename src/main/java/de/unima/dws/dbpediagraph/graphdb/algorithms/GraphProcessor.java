package de.unima.dws.dbpediagraph.graphdb.algorithms;

import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.GraphUtil;
import de.unima.dws.dbpediagraph.graphdb.GraphProvider;
import de.unima.dws.dbpediagraph.graphdb.util.GraphPrinter;

public class GraphProcessor {
	private static final Logger logger = LoggerFactory.getLogger(GraphProcessor.class);

	public static void main(String[] args) throws ConfigurationException {
		GraphProcessor processor = new GraphProcessor();
		// processor.getVertexTest("http://dbpedia.org/resource/Abraham_Lincoln");

		processor.graph.shutdown();
	}

	private final TransactionalGraph graph;

	public GraphProcessor() {
		graph = GraphProvider.getInstance().getGraph();
	}

	public void getVertexTest(String uri) {
		long startTime = System.currentTimeMillis();

		Vertex v = GraphUtil.getVertexByUri(graph, uri);
		GraphPrinter.printVertex(v);
		// GremlinPipeline pipe = new GremlinPipeline();
		// pipe.start(graph.getVertex(1)).out("knows").property("name");

		logger.info("Total time: %.2f sec", (System.currentTimeMillis() - startTime) / 1000.0);
	}

}
