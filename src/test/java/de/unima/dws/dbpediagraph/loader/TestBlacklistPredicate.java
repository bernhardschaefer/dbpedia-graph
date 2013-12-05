package de.unima.dws.dbpediagraph.loader;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.base.Predicate;

import de.unima.dws.dbpediagraph.graph.GraphConfig;

public class TestBlacklistPredicate {
	private static Predicate<Triple> predicate;

	private static Triple invalidSubject;
	private static Triple invalidObject;
	private static Triple validTriple;

	@BeforeClass
	public static void beforeClass() {
		predicate = new BlacklistTriplePredicate(GraphConfig.config());

		invalidSubject = Triple.fromStringUris("http://dbpedia.org/resource/Category:Topic",
				"http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://test.org/3");
		invalidObject = Triple.fromStringUris("http://test.org/1", "http://www.w3.org/1999/02/22-rdf-syntax-ns#type",
				"http://www.w3.org/2002/07/owl#Class");
		validTriple = Triple.fromStringUris("http://test.org/2", "http://www.w3.org/1999/02/22-rdf-syntax-ns#type",
				"http://do.not.filter.org");
	}

	@Test
	public void testFilter() {
		assertFalse(predicate.apply(invalidSubject));
		assertFalse(predicate.apply(invalidObject));

		assertTrue(predicate.apply(validTriple));
	}
}
