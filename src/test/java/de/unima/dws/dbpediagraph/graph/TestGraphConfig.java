package de.unima.dws.dbpediagraph.graph;

import static org.junit.Assert.assertNotNull;

import org.apache.commons.configuration.*;
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

	@Test
	public void testNewDisambiguator() throws ConfigurationException {
		Configuration config = new PropertiesConfiguration("test-config.properties");
		// simply check if no exceptions are thrown during reflection calls and result != null
		GraphDisambiguator<DefaultSurfaceForm, DefaultSense> disambiguator = GraphDisambiguatorFactory
				.newFromConfig(config);
		assertNotNull(disambiguator);
	}

}
