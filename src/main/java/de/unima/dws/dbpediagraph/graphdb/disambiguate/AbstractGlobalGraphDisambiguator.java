package de.unima.dws.dbpediagraph.graphdb.disambiguate;

import java.util.Collection;
import java.util.List;

import com.tinkerpop.blueprints.Graph;

public abstract class AbstractGlobalGraphDisambiguator implements GlobalGraphDisambiguator {

	@Override
	public List<WeightedSense> disambiguate(Collection<String> senses, Graph subgraph) {
		throw new UnsupportedOperationException("This operation is not supported for global disambiguators.");
	}

	@Override
	public List<WeightedSenseAssignments> disambiguateGlobal(List<List<String>> allWordsSenses, Graph subgraph) {
		// Example allWordSenses = {{drink1,drink2},{milk1,milk2,milk3}}

		// iteration over all possible sense assignments , e.g.:
		// 1st iteration: [drink1,milk1]
		// 2nd iteration: [drink1,milk2]
		// 3rd iteration: [drink1,milk3]
		// 4th iteration: [drink2,milk1]
		// ...

		// calculate global connectivity measure and add to result list

		// TODO implement genetic and simulated annealing functionality
		return null;
	}

	@Override
	abstract public Double globalConnectivityMeasure(Collection<String> senseAssignments, Graph sensegraph);
}
