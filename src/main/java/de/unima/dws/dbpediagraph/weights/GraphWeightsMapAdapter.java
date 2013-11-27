package de.unima.dws.dbpediagraph.weights;

import java.util.Map;

/**
 * {@link GraphWeights} adapter using a {@link Map}.
 * @author Bernhard Schäfer
 *
 */
public class GraphWeightsMapAdapter implements GraphWeights {
	private final Map<String, Integer> map;
	private final int edgeCount;
	
	public GraphWeightsMapAdapter(Map<String, Integer> map) { 
		this.map = map;
		edgeCount = map.get(PredObjOccsCounter.KEY_EDGE_COUNT);
	}

	@Override
	public double predicateWeight(String pred) {
		return ((double) map.get(pred)) / edgeCount;
	}

	@Override
	public double objectWeight(String obj) {
		return ((double) map.get(obj)) / edgeCount;
	}

	@Override
	public double predObjWeight(String pred, String obj) {
		return ((double) map.get(pred + obj)) / edgeCount;
	}

}
