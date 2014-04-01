package de.unima.dws.dbpediagraph.subgraph;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;
import com.tinkerpop.blueprints.*;

import de.unima.dws.dbpediagraph.graph.GraphConfig;
import de.unima.dws.dbpediagraph.util.LRUMap;

/**
 * Exploration threshold condition to prevent exploration of edges where the ingoing vertex has a high degree.
 * 
 * @author Bernhard Schäfer
 */
public class DegreeThreshold implements ExplorationThreshold {
	private static final Logger logger = LoggerFactory.getLogger(DegreeThreshold.class);

	private static final int DEFAULT_DEGREE_THRESHOLD = 10_000;

	private static final DegreeThreshold DEFAULT = new DegreeThreshold(DEFAULT_DEGREE_THRESHOLD);

	public static DegreeThreshold getDefault() {
		return DEFAULT;
	}

	private final int degreeThreshold;

	private final Map<String, Integer> vertexDegreeCache;
	private static final int DEFAULT_BLACKLIST_SIZE = 10_000;

	public DegreeThreshold(int maxDegree) {
		this(maxDegree, new LRUMap<String, Integer>(DEFAULT_BLACKLIST_SIZE));
	}

	public DegreeThreshold(int degreeThreshold, Map<String, Integer> vertexDegreeCache) {
		this.degreeThreshold = degreeThreshold;
		this.vertexDegreeCache = vertexDegreeCache;
	}

	@Override
	public boolean isBelowThreshold(Vertex v, Edge e) {
		String uri = v.getProperty(GraphConfig.URI_PROPERTY);
		Integer degree = vertexDegreeCache.get(uri);
		if (degree == null) {
			degree = Iterables.size(v.getEdges(Direction.BOTH));
			vertexDegreeCache.put(uri, degree);
		}

		boolean isBelowThreshold = degree <= degreeThreshold;
		if (!isBelowThreshold)
			logger.debug("Vertex {} with degree {} is above threshold {}", uri, degree, degreeThreshold);
		return isBelowThreshold;
	}

}
