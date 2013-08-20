package de.unima.dws.dbpediagraph.graphdb;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestUriShortener {

	private Map<String, String> uriToPrefix;

	@Before
	public void setUp() throws Exception {
		uriToPrefix = new HashMap<String, String>();
		uriToPrefix.put("http://dbpedia.org/resource/Animal_Farm", "dbr:Animal_Farm");
		uriToPrefix.put("http://dbpedia.org/ontology/Person", "dbo:Person");
		uriToPrefix.put("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "rdf:type");
		uriToPrefix.put("http://purl.org/dc/terms/subject", "dcterms:");
		uriToPrefix.put("http://xmlns.com/foaf/0.1/Person", "foaf:Person");
		uriToPrefix.put("http://www.w3.org/2004/02/skos/core#subject", "skos:subject");
		uriToPrefix.put("http://www.w3.org/2003/01/geo/wgs84_pos#lat", "pos:lat");
		uriToPrefix.put("http://purl.org/dc/elements/1.1/description", "dc:description");
		uriToPrefix.put("http://www.georss.org/georss/point", "poi:");
	}

	@After
	public void tearDown() throws Exception {
		uriToPrefix.clear();
		uriToPrefix = null;
	}

	@Test
	public void testShortening() {
		for (Entry<String, String> e : uriToPrefix.entrySet()) {
			String uri = e.getKey();
			String shortUri = e.getValue();
			String shortenedUri = UriShortener.shorten(uri);
			assertEquals(shortenedUri, shortUri);
		}
	}

	@Test
	public void testUnshortening() {
		for (Entry<String, String> e : uriToPrefix.entrySet()) {
			String uri = e.getKey();
			String shortUri = e.getValue();
			String unshortenedUri = UriShortener.unshorten(shortUri);
			assertEquals(unshortenedUri, uri);
		}

	}

}
