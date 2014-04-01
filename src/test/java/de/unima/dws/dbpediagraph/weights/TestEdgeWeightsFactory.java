package de.unima.dws.dbpediagraph.weights;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.apache.commons.configuration.*;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unima.dws.dbpediagraph.weights.EdgeWeightsFactory.EdgeWeightsType;

/**
 * @author Bernhard Schäfer
 */
public class TestEdgeWeightsFactory {
	private static Configuration configCombIC;
	private static Map<String, Integer> occCounts;

	@BeforeClass
	public static void beforeClass() {
		String fileName = "test-weights/testconfig-combic.properties";
		try {
			configCombIC = new PropertiesConfiguration(fileName);
		} catch (ConfigurationException e) {
			throw new IllegalArgumentException("Test file could not be loaded.", e);
		}

		occCounts = DummyOccurrenceCounts.DUMMY_MAP;
	}

	@Test
	public void testFromConfig() {
		EdgeWeights edgeWeights = EdgeWeightsFactory.fromConfig(configCombIC, occCounts);
		assertTrue(edgeWeights instanceof CombinedInformationContent);
	}

	@Test
	public void testFromEdgeWeightsType() {
		for (EdgeWeightsType type : EdgeWeightsType.values()) {
			EdgeWeights edgeWeights = EdgeWeightsFactory.fromEdgeWeightsType(type, occCounts);
			assertNotNull(edgeWeights);
		}
	}

}
