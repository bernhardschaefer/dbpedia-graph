package de.unima.dws.dbpediagraph.weights;

import java.io.File;

import de.unima.dws.dbpediagraph.util.BerkeleyDB;
import de.unima.dws.dbpediagraph.util.PersistedMap;

public class BerkeleyDBGraphWeights implements GraphWeights {
	private final StoredMappingGraphWeights graphWeights;

	public BerkeleyDBGraphWeights(PersistedMap<String, Double> predicateWeights,
			PersistedMap<String, Double> objectWeights, PersistedMap<String, Double> predObjWeights) {
		graphWeights = new StoredMappingGraphWeights(predicateWeights, objectWeights, predObjWeights);
	}

	public static BerkeleyDBGraphWeights fromSingleDB(String location, String dbname) {
		PersistedMap<String, Double> db = new BerkeleyDB.Builder<>(new File(location + dbname), dbname, String.class,
				Double.class).build();
		return new BerkeleyDBGraphWeights(db, db, db);
	}

	public BerkeleyDBGraphWeights(final String location, String predDBName, String objDBName, String predObjDBName) {
		// String predDBName = "pred";
		BerkeleyDB<String, Double> predicateWeights = new BerkeleyDB.Builder<>(new File(location + predDBName),
				predDBName, String.class, Double.class).build();
		// String objDBName = "obj";
		BerkeleyDB<String, Double> objectWeights = new BerkeleyDB.Builder<>(new File(location + objDBName), objDBName,
				String.class, Double.class).build();
		// String predObjDBName = "predobj";
		BerkeleyDB<String, Double> predObjWeights = new BerkeleyDB.Builder<>(new File(location + predObjDBName),
				predObjDBName, String.class, Double.class).build();
		graphWeights = new StoredMappingGraphWeights(predicateWeights, objectWeights, predObjWeights);
	}

	@Override
	public double predicateWeight(String pred) {
		return graphWeights.predicateWeight(pred);
	}

	@Override
	public double objectWeight(String obj) {
		return graphWeights.objectWeight(obj);
	}

	@Override
	public double predObjWeight(String pred, String obj) {
		return graphWeights.predObjWeight(pred, obj);
	}

}
