package de.unima.dws.dbpediagraph.disambiguate;

import static org.junit.Assert.*;

import org.apache.commons.configuration.*;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestPriorStrategy {
	private static Configuration config1;
	private static Configuration config2;

	@BeforeClass
	public static void beforeClass() throws ConfigurationException {
		String prefix = "test-prior-strategy/testconfig";
		String suffix = ".properties";
		config1 = new PropertiesConfiguration(prefix + "1" + suffix);
		config2 = new PropertiesConfiguration(prefix + "2" + suffix);
	}

	@Test
	public void testFromConfig() {
		assertNull(PriorStrategy.fromConfig(config1));
		PriorStrategy actual = PriorStrategy.fromConfig(config2);
		assertEquals(PriorStrategy.CONFIDENCE_FALLBACK, actual);
	}

}
