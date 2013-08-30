package de.unima.dws.dbpediagraph.graphdb;

import java.util.List;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unima.dws.dbpediagraph.graphdb.disambiguate.LocalDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.WeightedSense;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstruction;

public class LocalDisambiguationTestData extends AbstractDisambiguationTestData {
	private static final Logger logger = LoggerFactory.getLogger(LocalDisambiguationTestData.class);

	/** Local Connectivity Measure Results for the example from Navigli&Lapata (2010) */
	private static final String NL_LOCAL_RESULTS = "/nl-local-test.results";

	private final LocalDisambiguator disambiguator;
	protected List<WeightedSense> weightedUris;

	public LocalDisambiguationTestData(LocalDisambiguator disambiguator, SubgraphConstruction subgraphConstruction) {
		super(subgraphConstruction, NL_LOCAL_RESULTS);
		this.disambiguator = disambiguator;
		weightedUris = disambiguator.disambiguate(GraphUtil.getUrisOfVertices(senses), subgraph);
	}

	@Override
	public void checkDisambiguationResults() {
		for (WeightedSense wUri : weightedUris) {
			double expected = measureResults.get(wUri.getSense()).get(disambiguator.getType());
			logger.info("uri: {} actual weight: {} expected weight: {}", wUri.getSense(), wUri.getWeight(), expected);
			Assert.assertEquals(expected, wUri.getWeight(), DELTA);
		}

	}

	public List<WeightedSense> getWeightedUris() {
		return weightedUris;
	}

}
