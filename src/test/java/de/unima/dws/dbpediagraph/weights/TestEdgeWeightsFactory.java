package de.unima.dws.dbpediagraph.weights;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.configuration.*;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unima.dws.dbpediagraph.weights.EdgeWeightsFactory.EdgeWeightsType;

public class TestEdgeWeightsFactory {

	private static Configuration configCombIC;

	@BeforeClass
	public static void beforeClass() {
		String fileName = "test-weights/testconfig-combic.properties";
		try {
			configCombIC = new PropertiesConfiguration(fileName);
		} catch (ConfigurationException e) {
			throw new IllegalArgumentException("Test file could not be loaded.", e);
		}
	}

	@Test
	public void testFromConfig() {
		EdgeWeights edgeWeights = EdgeWeightsFactory.dbpediaImplFromConfig(configCombIC);
		assertTrue(edgeWeights instanceof CombinedInformationContent);
	}
	
	@Test
	public void testFromEdgeWeightsType() {
		for (EdgeWeightsType type : EdgeWeightsType.values()) {
			EdgeWeights edgeWeights = EdgeWeightsFactory.dbpediaWeightsfromEdgeWeightsType(type);
			assertNotNull(edgeWeights);
		}
	}

}
