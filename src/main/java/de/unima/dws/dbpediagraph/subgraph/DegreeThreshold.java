package de.unima.dws.dbpediagraph.subgraph;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graph.GraphConfig;
import de.unima.dws.dbpediagraph.util.CollectionUtils;
import de.unima.dws.dbpediagraph.util.LRUMap;

/**
 * @author Bernhard Schäfer
 */
public class DegreeThreshold implements ExplorationThreshold {
	private static final Logger logger = LoggerFactory.getLogger(DegreeThreshold.class);

	private static final int DEFAULT_DEGREE = 10000;
	private static final DegreeThreshold DEFAULT = new DegreeThreshold(DEFAULT_DEGREE);

	public static DegreeThreshold getDefault() {
		return DEFAULT;
	}

	private final int maxDegree;

	private final Map<String, Integer> blacklist;
	private static final int DEFAULT_BLACKLIST_SIZE = 1000;

	public DegreeThreshold(int maxDegree) {
		this(maxDegree, new LRUMap<String, Integer>(DEFAULT_BLACKLIST_SIZE));
	}

	public DegreeThreshold(int maxDegree, Map<String, Integer> blacklist) {
		this.maxDegree = maxDegree;
		this.blacklist = blacklist;
	}

	@Override
	public boolean isBelowThreshold(Vertex v, Edge e) {
		String uri = v.getProperty(GraphConfig.URI_PROPERTY);
		if (blacklist.containsKey(uri))
			return false;
		int degree = CollectionUtils.iterableItemCount(v.getEdges(Direction.BOTH));
		boolean isBelowThreshold = degree <= maxDegree;
		if (!isBelowThreshold) {
			logger.debug("Vertex {} with degree {} is above threshold {}", uri, degree, maxDegree);
			blacklist.put(uri, degree);
		}
		return isBelowThreshold;
	}

}
