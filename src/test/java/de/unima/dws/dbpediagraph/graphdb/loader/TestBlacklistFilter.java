package de.unima.dws.dbpediagraph.graphdb.loader;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;

public class TestBlacklistFilter {
	private static LoadingStatementFilter filter;

	private static Statement invalidSubject;
	private static Statement invalidObject;
	private static Statement validStatement;

	@BeforeClass
	public static void beforeClass() {
		filter = new DBpediaBlacklistLoadingStatementFilter();

		invalidSubject = fromStringUris("http://dbpedia.org/resource/Category:Topic",
				"http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://test.org/3");
		invalidObject = fromStringUris("http://test.org/1", "http://www.w3.org/1999/02/22-rdf-syntax-ns#type",
				"http://www.w3.org/2002/07/owl#Class");
		validStatement = fromStringUris("http://test.org/2", "http://www.w3.org/1999/02/22-rdf-syntax-ns#type",
				"http://do.not.filter.org");
	}

	private static Statement fromStringUris(String sub, String pred, String obj) {
		return new StatementImpl(new URIImpl(sub), new URIImpl(pred), new URIImpl(obj));
	}

	@Test
	public void testFilter() {
		assertFalse(filter.isValidStatement(invalidSubject));
		assertFalse(filter.isValidStatement(invalidObject));

		assertTrue(filter.isValidStatement(validStatement));
	}
}
