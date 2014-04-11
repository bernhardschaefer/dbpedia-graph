package de.unima.dws.dbpediagraph.weights;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;

import de.unima.dws.dbpediagraph.graph.GraphConfig;
import de.unima.dws.dbpediagraph.graph.Graphs;

/**
 * {@link EdgeWeights} adapter using a {@link Map} of occurrences counts.
 * 
 * @author Bernhard Schäfer
 * 
 */
public abstract class AbstractEdgeWeightOccsCountAdapter implements EdgeWeights {
	protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractEdgeWeightOccsCountAdapter.class);

	private final Map<String, Integer> occCounts;
	private final int totalEdges;

	public AbstractEdgeWeightOccsCountAdapter(Map<String, Integer> occCounts) {
		this.occCounts = occCounts;
		totalEdges = occCounts.get(PredObjOccsCounter.KEY_EDGE_COUNT);
	}

	@Override
	abstract public Double transform(Edge e);

	/**
	 * @return prior probability (number of occurrences) of the short URI.
	 */
	protected double p(String shortUri) {
		int shortUriCount = checkNotNull(occCounts.get(shortUri),
				"The graph occs count is corrupt. URI %s has no count.", shortUri);
		double p = (double) shortUriCount / totalEdges;
		if (LOGGER.isTraceEnabled())
			LOGGER.trace(String.format("p(%s)=%d/%d=%.6f%%", shortUri, shortUriCount, totalEdges, p * 100));
		return p;
	}

	/**
	 * @return the information content of the short URI.
	 */
	protected double ic(String shortUri) {
		double ic = -1 * Math.log(p(shortUri));
		if (LOGGER.isTraceEnabled())
			LOGGER.trace(String.format("ic(%s)=%.2f", shortUri, ic));
		return ic;
	}

	protected static String getPred(Edge e) {
		return e.getProperty(GraphConfig.URI_PROPERTY);
	}

	protected static String getObj(Edge e) {
		return Graphs.shortUriOfVertex(e.getVertex(Direction.IN));
	}

	protected void logEdgeScore(Edge e, double score) {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug(String.format("%s(%s) = %.2f", type(), Graphs.edgeToString(e), score));
	}
}
