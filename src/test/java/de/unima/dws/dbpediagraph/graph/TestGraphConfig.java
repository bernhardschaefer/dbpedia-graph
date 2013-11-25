package de.unima.dws.dbpediagraph.graph;

import static org.junit.Assert.assertNotNull;

import org.apache.commons.configuration.Configuration;
import org.junit.Test;

import de.unima.dws.dbpediagraph.disambiguate.GlobalGraphDisambiguator;
import de.unima.dws.dbpediagraph.disambiguate.LocalGraphDisambiguator;
import de.unima.dws.dbpediagraph.graph.GraphConfig;
import de.unima.dws.dbpediagraph.graph.GraphType;
import de.unima.dws.dbpediagraph.model.*;
import de.unima.dws.dbpediagraph.subgraph.SubgraphConstructionSettings;

/**
 * Tests for {@link GraphConfig}
 * 
 * @author Bernhard Schäfer
 * 
 */
public class TestGraphConfig {

	private Configuration config = GraphConfig.config();
	private SubgraphConstructionSettings subgraphConstructionSettings = SubgraphConstructionSettings.getDefault();
	private ModelFactory<DefaultSurfaceForm, DefaultSense> factory = DefaultModelFactory.INSTANCE;
	private GraphType graphType = GraphType.DIRECTED_GRAPH;

	@Test
	public void testNewGlobalDisambiguator() {
		// simply check if no exceptions are thrown during reflection calls and result != null
		GlobalGraphDisambiguator<DefaultSurfaceForm, DefaultSense> disambiguator = GraphConfig.newGlobalDisambiguator(
				config, subgraphConstructionSettings, factory);
		assertNotNull(disambiguator);
	}

	@Test
	public void testNewLocalDisambiguator() {
		// simply check if no exceptions are thrown during reflection calls and result != null
		LocalGraphDisambiguator<DefaultSurfaceForm, DefaultSense> disambiguator = GraphConfig.newLocalDisambiguator(
				graphType, factory);
		assertNotNull(disambiguator);
	}
}
