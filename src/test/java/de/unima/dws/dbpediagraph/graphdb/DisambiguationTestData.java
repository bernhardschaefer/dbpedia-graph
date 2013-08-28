package de.unima.dws.dbpediagraph.graphdb;

import java.util.List;

import com.tinkerpop.blueprints.Graph;

import de.unima.dws.dbpediagraph.graphdb.disambiguate.Disambiguator;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.WeightedUri;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstruction;

public class DisambiguationTestData extends SubgraphTestData {

	private final Graph subgraph;
	private final List<WeightedUri> weightedUris;

	public DisambiguationTestData(Disambiguator disambiguator, SubgraphConstruction subgraphConstruction) {
		super();
		subgraphConstruction.setGraph(graph);
		subgraph = subgraphConstruction.createSubgraph(senses);
		weightedUris = disambiguator.disambiguate(GraphUtil.getUrisOfVertices(senses), subgraph);
	}

	@Override
	public void close() {
		super.close();
		if (subgraph != null)
			subgraph.shutdown();
	}

	public Graph getSubgraph() {
		return subgraph;
	}

	public List<WeightedUri> getWeightedUris() {
		return weightedUris;
	}

}
