package de.unima.dws.dbpediagraph.weights;

/**
 * @author Bernhard Schäfer
 */
public interface GraphWeights {
	double predicateWeight(String pred);

	double objectWeight(String obj);

	double predObjWeight(String pred, String obj);
}
