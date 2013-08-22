package de.unima.dws.dbpediagraph.graphdb.algorithms;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.GraphConfig;
import de.unima.dws.dbpediagraph.graphdb.GraphHelper;

public class SubgraphDetection extends GraphAlgorithm {
	private static final Logger logger = LoggerFactory.getLogger(SubgraphDetection.class);
	private static final int LIMIT = 5;

	public static void main(String[] args) {
		SubgraphDetection sd = new SubgraphDetection();
		sd.pathTest();
		sd.shutdown();
	}

	private final LimitedDFS dfs;

	public SubgraphDetection() {
		dfs = new LimitedDFS(graph, LIMIT);
	}

	public void pathTest() {
		Collection<Vertex> vertices = GraphHelper.getTestVertices(graph);
		for (Vertex v1 : vertices) {
			for (Vertex v2 : vertices) {
				if (!v1.equals(v2)) {
					long startTime = System.nanoTime();
					List<Edge> path = dfs.findPathToTarget(v1, v2);
					long duration = System.nanoTime() - startTime;

					logger.info("Path length {} from {} to {} in {} ms", path.size(),
							v1.getProperty(GraphConfig.URI_PROPERTY), v2.getProperty(GraphConfig.URI_PROPERTY),
							duration / 1_000_000.0);
					if (!path.isEmpty()) {

						StringBuilder builder = new StringBuilder();
						for (Edge e : path) {
							builder.append(e.getVertex(Direction.OUT).getProperty(GraphConfig.URI_PROPERTY))
									.append("--").append(e.getProperty(GraphConfig.URI_PROPERTY)).append("->");
						}
						builder.append(v2.getProperty(GraphConfig.URI_PROPERTY));
						logger.info(builder.toString());
					}
					logger.info("");
				}
			}
		}
	}

}
