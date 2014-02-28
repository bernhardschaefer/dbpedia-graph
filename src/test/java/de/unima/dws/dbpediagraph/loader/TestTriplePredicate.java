package de.unima.dws.dbpediagraph.loader;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.configuration.*;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.base.Predicate;

import de.unima.dws.dbpediagraph.graph.GraphConfig;
import de.unima.dws.dbpediagraph.util.EnumUtils;

public class TestTriplePredicate {

	private static Configuration config1;
	private static Configuration config2;
	private static Configuration config3;

	private static final String configKey = "loading.filter.impl";

	private static final Triple categoryObjectTriple = Triple.fromStringUris("http://test.org/3",
			"http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://dbpedia.org/resource/Category:Topic");
	private static final Triple ontologyObjectTriple = Triple.fromStringUris("http://test.org/1",
			"http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://www.w3.org/2002/07/owl#Class");
	private static final Triple rdfTypeTriple = Triple.fromStringUris("http://test.org/2",
			"http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://do.not.filter.org");

	private static final List<Triple> categoryValidTriples = Arrays.asList( //
			Triple.fromStringUris("http://dbpedia.org/resource/Ayn_Rand", "http://purl.org/dc/terms/subject",
					"http://dbpedia.org/resource/Category:Ayn_Rand"), //
			Triple.fromStringUris("http://dbpedia.org/resource/Ayn_Rand", "http://purl.org/dc/terms/subject",
					"http://dbpedia.org/resource/Category:American_anti-communists"));

	private static final List<Triple> categoryInvalidTriples = Arrays.asList( //
			Triple.fromStringUris("http://dbpedia.org/resource/Ayn_Rand", "http://purl.org/dc/terms/subject",
					"http://dbpedia.org/resource/Category:1905_births"), // "^\\d+.*$"
			Triple.fromStringUris("http://dbpedia.org/resource/Ayn_Rand", "http://purl.org/dc/terms/subject",
					"http://dbpedia.org/resource/Category:20th-century_philosophers"), // "^\\d+.*$"
			Triple.fromStringUris("http://dbpedia.org/resource/Skateboarding_Hall_of_Fame",
					"http://purl.org/dc/terms/subject",
					"http://dbpedia.org/resource/Category:Awards_established_in_2009"), // "^.*\\d+$"
			Triple.fromStringUris("http://dbpedia.org/resource/Abbotsford_House", "http://purl.org/dc/terms/subject",
					"http://dbpedia.org/resource/Category:Listed_houses_in_Scotland"), // "^list.*"
			Triple.fromStringUris("http://dbpedia.org/resource/Decision_analysis_cycle",
					"http://purl.org/dc/terms/subject", "http://dbpedia.org/resource/Category:Decision_theory_stubs") // "^.*(wiki|disambiguation|templates|portal|categories|articles|pages|redirect|navigational_boxes|infoboxes|stub).*"
			);

	@BeforeClass
	public static void beforeClass() throws ConfigurationException {
		String prefix = "test-loader/testconfig";
		String suffix = ".properties";
		config1 = new PropertiesConfiguration(prefix + "1" + suffix);
		config2 = new PropertiesConfiguration(prefix + "2" + suffix);
		config3 = new PropertiesConfiguration(prefix + "3" + suffix);
	}

	@Test
	public void testNonCategoryPredicate() {
		Predicate<Triple> pred = TriplePredicate.NON_CATEGORY;
		assertFalse(pred.apply(categoryObjectTriple));
		assertTrue(pred.apply(ontologyObjectTriple));
		assertTrue(pred.apply(rdfTypeTriple));
	}

	@Test
	public void testRegexpCategoryPredicate() {
		Predicate<Triple> pred = TriplePredicate.REGEXP_CATEGORY;
		for (Triple t : categoryValidTriples)
			assertTrue(pred.apply(t));
		for (Triple t : categoryInvalidTriples)
			assertFalse(pred.apply(t));
	}

	@Test
	public void testBlacklistPredicate() {
		Predicate<Triple> pred = new BlacklistTriplePredicate(GraphConfig.config());
		assertFalse(pred.apply(categoryObjectTriple));
		assertFalse(pred.apply(ontologyObjectTriple));

		assertTrue(pred.apply(rdfTypeTriple));
	}

	@Test
	public void testFromEnumType() {
		for (Predicate<Triple> pred : TriplePredicate.values()) {
			assertNotNull(pred);
		}
	}

	@Test
	public void testConfig1ParsedPredicates() {
		// config1 --> BLACKLIST, COMPLETE
		List<TriplePredicate> loadingTypes = EnumUtils.enumsfromConfig(TriplePredicate.class, config1, configKey);
		assertTrue(loadingTypes.size() == 2);
		assertTrue(loadingTypes.get(0).equals(TriplePredicate.BLACKLIST));
		assertTrue(loadingTypes.get(1).equals(TriplePredicate.COMPLETE));
	}

	@Test
	public void testConfig1Predicates() {
		Predicate<Triple> loadingTypes = TriplePredicate.fromConfig(config1);
		assertNotNull(loadingTypes);
	}

	@Test
	public void testConfig2ParsedPredicates() {
		// config2 --> COMPLETE
		List<TriplePredicate> loadingTypes = EnumUtils.enumsfromConfig(TriplePredicate.class, config2, configKey);
		assertTrue(loadingTypes.size() == 1);
		assertTrue(loadingTypes.get(0).equals(TriplePredicate.COMPLETE));
	}

	@Test
	public void testConfig3ParsedPredicates() {
		// config3 --> no key and value
		List<TriplePredicate> loadingTypes = EnumUtils.enumsfromConfig(TriplePredicate.class, config3, configKey);
		assertTrue(loadingTypes.size() == 0);
	}
}
