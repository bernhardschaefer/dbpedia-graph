package de.unima.dws.dbpediagraph.graphdb.disambiguate.global;

import java.util.Collection;

import com.tinkerpop.blueprints.Graph;

import de.unima.dws.dbpediagraph.graphdb.disambiguate.AbstractGlobalDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.ConnectivityMeasure;

public class Compactness extends AbstractGlobalDisambiguator {

	@Override
	public ConnectivityMeasure getType() {
		return ConnectivityMeasure.Compactness;
	}

	@Override
	public Double globalConnectivityMeasure(Collection<String> senseAssignments, Graph subgraph) {

		return null;
	}

}
