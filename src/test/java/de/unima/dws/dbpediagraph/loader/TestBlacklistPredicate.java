package de.unima.dws.dbpediagraph.loader;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;

import com.google.common.base.Predicate;

import de.unima.dws.dbpediagraph.graph.GraphConfig;

public class TestBlacklistPredicate {
	private static Predicate<Triple> predicate;

	private static Triple invalidSubject;
	private static Triple invalidObject;
	private static Triple validStatement;

	@BeforeClass
	public static void beforeClass() {
		predicate = new BlacklistStatementPredicate(GraphConfig.config());

		invalidSubject = new Triple("http://dbpedia.org/resource/Category:Topic",
				"http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://test.org/3");
		invalidObject = new Triple("http://test.org/1", "http://www.w3.org/1999/02/22-rdf-syntax-ns#type",
				"http://www.w3.org/2002/07/owl#Class");
		validStatement = new Triple("http://test.org/2", "http://www.w3.org/1999/02/22-rdf-syntax-ns#type",
				"http://do.not.filter.org");
	}

	static Statement fromStringUris(String sub, String pred, String obj) {
		return new StatementImpl(new URIImpl(sub), new URIImpl(pred), new URIImpl(obj));
	}

	@Test
	public void testFilter() {
		assertFalse(predicate.apply(invalidSubject));
		assertFalse(predicate.apply(invalidObject));

		assertTrue(predicate.apply(validStatement));
	}
}
