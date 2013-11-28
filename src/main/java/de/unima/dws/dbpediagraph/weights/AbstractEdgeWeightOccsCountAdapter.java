package de.unima.dws.dbpediagraph.weights;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import com.tinkerpop.blueprints.Edge;

/**
 * {@link EdgeWeights} adapter using a {@link Map} of occurrences counts.
 * 
 * @author Bernhard Schäfer
 * 
 */
public abstract class AbstractEdgeWeightOccsCountAdapter implements EdgeWeights {

	private final Map<String, Integer> occCounts;
	private final double totalEdges;

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
		return shortUriCount / totalEdges;
	}

	/**
	 * @return the information content of the short URI.
	 */
	protected double ic(String shortUri) {
		return -1 * Math.log(p(shortUri));
	}
}
