package de.unima.dws.dbpediagraph.graph;

import static org.junit.Assert.assertNotNull;

import org.apache.commons.configuration.Configuration;
import org.junit.Test;

import de.unima.dws.dbpediagraph.disambiguate.GraphDisambiguator;
import de.unima.dws.dbpediagraph.disambiguate.GraphDisambiguatorFactory;
import de.unima.dws.dbpediagraph.model.DefaultSense;
import de.unima.dws.dbpediagraph.model.DefaultSurfaceForm;

/**
 * Tests for {@link GraphConfig}
 * 
 * @author Bernhard Schäfer
 * 
 */
public class TestGraphConfig {

	private final Configuration config = GraphConfig.config();

	@Test
	public void testNewDisambiguator() {
		// simply check if no exceptions are thrown during reflection calls and result != null
		GraphDisambiguator<DefaultSurfaceForm, DefaultSense> disambiguator = GraphDisambiguatorFactory
				.newFromConfig(config);
		assertNotNull(disambiguator);
	}

}
