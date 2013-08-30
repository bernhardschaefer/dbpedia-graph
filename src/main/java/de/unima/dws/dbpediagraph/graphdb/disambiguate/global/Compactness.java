package de.unima.dws.dbpediagraph.graphdb.disambiguate.global;

import java.util.Set;

import com.tinkerpop.blueprints.Graph;

import de.unima.dws.dbpediagraph.graphdb.disambiguate.AbstractGlobalDisambiguator;

public class Compactness extends AbstractGlobalDisambiguator {

	@Override
	public Double globalConnectivityMeasure(Set<String> senseAssignments, Graph subgraph) {

		return null;
	}

}
