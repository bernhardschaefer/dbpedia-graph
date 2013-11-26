package de.unima.dws.dbpediagraph.weights;

import de.unima.dws.dbpediagraph.util.PersistedMap;

public class StoredMappingGraphWeights implements GraphWeights {

	private final PersistedMap<String, Double> predicateWeights;
	private final PersistedMap<String, Double> objectWeights;
	private final PersistedMap<String, Double> predObjWeights;

	public StoredMappingGraphWeights(PersistedMap<String, Double> predicateWeights,
			PersistedMap<String, Double> objectWeights, PersistedMap<String, Double> predObjWeights) {
		this.predicateWeights = predicateWeights;
		this.objectWeights = objectWeights;
		this.predObjWeights = predObjWeights;
	}

	@Override
	public double predicateWeight(String pred) {
		return predicateWeights.get(pred);
	}

	@Override
	public double objectWeight(String obj) {
		return objectWeights.get(obj);
	}

	@Override
	public double predObjWeight(String pred, String obj) {
		return predObjWeights.get(pred + obj);
	}

}
