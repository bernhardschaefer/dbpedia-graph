package de.unima.dws.dbpediagraph.graph;

import static org.junit.Assert.assertNotNull;

import org.apache.commons.configuration.Configuration;
import org.junit.Test;

import de.unima.dws.dbpediagraph.disambiguate.GlobalGraphDisambiguator;
import de.unima.dws.dbpediagraph.disambiguate.LocalGraphDisambiguator;
import de.unima.dws.dbpediagraph.model.DefaultSense;
import de.unima.dws.dbpediagraph.model.DefaultSurfaceForm;
import de.unima.dws.dbpediagraph.subgraph.SubgraphConstructionSettings;

/**
 * Tests for {@link GraphConfig}
 * 
 * @author Bernhard Schäfer
 * 
 */
public class TestGraphConfig {

	private final Configuration config = GraphConfig.config();
	private final SubgraphConstructionSettings subgraphConstructionSettings = SubgraphConstructionSettings.getDefault();
	private final GraphType graphType = GraphType.DIRECTED_GRAPH;

	@Test
	public void testNewGlobalDisambiguator() {
		// simply check if no exceptions are thrown during reflection calls and result != null
		GlobalGraphDisambiguator<DefaultSurfaceForm, DefaultSense> disambiguator = GraphConfig.newGlobalDisambiguator(
				config, subgraphConstructionSettings);
		assertNotNull(disambiguator);
	}

	@Test
	public void testNewLocalDisambiguator() {
		// simply check if no exceptions are thrown during reflection calls and result != null
		LocalGraphDisambiguator<DefaultSurfaceForm, DefaultSense> disambiguator = GraphConfig
				.newLocalDisambiguator(graphType);
		assertNotNull(disambiguator);
	}
}
