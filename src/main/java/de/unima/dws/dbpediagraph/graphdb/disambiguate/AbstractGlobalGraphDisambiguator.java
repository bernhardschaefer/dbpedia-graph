package de.unima.dws.dbpediagraph.graphdb.disambiguate;

import java.util.Collection;

import com.tinkerpop.blueprints.Graph;

import de.unima.dws.dbpediagraph.graphdb.model.Sense;
import de.unima.dws.dbpediagraph.graphdb.model.SurfaceForm;

public abstract class AbstractGlobalGraphDisambiguator<T extends SurfaceForm, U extends Sense> implements
		GlobalGraphDisambiguator<T, U> {

	// @Override
	// public List<WeightedSenseAssignments> disambiguateGlobal(List<List<String>> allWordsSenses, Graph subgraph) {
	// // Example allWordSenses = {{drink1,drink2},{milk1,milk2,milk3}}
	//
	// // iteration over all possible sense assignments , e.g.:
	// // 1st iteration: [drink1,milk1]
	// // 2nd iteration: [drink1,milk2]
	// // 3rd iteration: [drink1,milk3]
	// // 4th iteration: [drink2,milk1]
	// // ...
	//
	// // calculate global connectivity measure and add to result list
	//
	// // TODO implement genetic and simulated annealing functionality
	// return null;
	// }

	@Override
	abstract public Double globalConnectivityMeasure(Collection<String> senseAssignments, Graph sensegraph);
}
