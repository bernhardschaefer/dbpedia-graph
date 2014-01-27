package de.unima.dws.dbpediagraph.loader;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.configuration.*;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.base.Predicate;

import de.unima.dws.dbpediagraph.loader.TriplePredicateFactory.LoadingType;
import de.unima.dws.dbpediagraph.util.EnumUtils;

public class TestTriplePredicateFactory {

	private static Configuration config1;
	private static Configuration config2;
	private static Configuration config3;

	private static final String configKey = "loading.filter.impl";

	@BeforeClass
	public static void beforeClass() throws ConfigurationException {
		String prefix = "test-loader/testconfig";
		String suffix = ".properties";
		config1 = new PropertiesConfiguration(prefix + "1" + suffix);
		config2 = new PropertiesConfiguration(prefix + "2" + suffix);
		config3 = new PropertiesConfiguration(prefix + "3" + suffix);
	}

	@Test
	public void testFromLoadingType() {
		for (LoadingType type : LoadingType.values()) {
			Predicate<Triple> pred = type.getPredicate();
			assertNotNull(pred);
		}
	}

	@Test
	public void testConfig1LoadingTypes() {
		// config1 --> BLACKLIST, COMPLETE
		List<LoadingType> loadingTypes = EnumUtils.enumsfromConfig(LoadingType.class, config1, configKey);
		assertTrue(loadingTypes.size() == 2);
		assertTrue(loadingTypes.get(0).equals(LoadingType.BLACKLIST));
		assertTrue(loadingTypes.get(1).equals(LoadingType.COMPLETE));
	}

	@Test
	public void testConfig1Predicates() {
		Predicate<Triple> loadingTypes = TriplePredicateFactory.fromConfig(config1);
		assertNotNull(loadingTypes);
	}

	@Test
	public void testConfig2LoadingTypes() {
		// config2 --> COMPLETE
		List<LoadingType> loadingTypes = EnumUtils.enumsfromConfig(LoadingType.class, config2, configKey);
		assertTrue(loadingTypes.size() == 1);
		assertTrue(loadingTypes.get(0).equals(LoadingType.COMPLETE));
	}

	@Test
	public void testConfig3LoadingTypes() {
		// config3 --> no key and value
		List<LoadingType> loadingTypes = EnumUtils.enumsfromConfig(LoadingType.class, config3, configKey);
		assertTrue(loadingTypes.size() == 0);
	}
}
