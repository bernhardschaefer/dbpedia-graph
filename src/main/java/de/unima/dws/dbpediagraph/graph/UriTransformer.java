package de.unima.dws.dbpediagraph.graph;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.Map.Entry;

import com.tinkerpop.blueprints.Graph;

/**
 * Implementation for shortening and unshortening URIs using prefixes to reduce the persisted size of the {@link Graph}.
 * Also performs URI decoding.
 * 
 * @author Bernhard Schäfer
 * 
 */
public final class UriTransformer {
	public static final String DBPEDIA_RESOURCE_PREFIX = "http://dbpedia.org/resource/";
	
	private static final Map<String, String> URI_TO_PREFIX;
	static {
		URI_TO_PREFIX = new LinkedHashMap<String, String>();

		// subject and objects are all dbpedia resources
		URI_TO_PREFIX.put(DBPEDIA_RESOURCE_PREFIX, "dbr:");

		// top predicates extracted from dumps
		URI_TO_PREFIX.put("http://dbpedia.org/ontology/", "dbo:");
		URI_TO_PREFIX.put("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf:");
		URI_TO_PREFIX.put("http://purl.org/dc/terms/subject", "dcterms:");
		URI_TO_PREFIX.put("http://xmlns.com/foaf/0.1/", "foaf:");
		URI_TO_PREFIX.put("http://www.w3.org/2004/02/skos/core#", "skos:");
		URI_TO_PREFIX.put("http://www.w3.org/2003/01/geo/wgs84_pos#", "pos:");
		URI_TO_PREFIX.put("http://purl.org/dc/elements/1.1/", "dc:");
		URI_TO_PREFIX.put("http://www.georss.org/georss/point", "poi:");

		// other prefixes from http://dbpedia.org/snorql/
		URI_TO_PREFIX.put("http://www.w3.org/2002/07/owl#", "owl:");
		URI_TO_PREFIX.put("http://www.w3.org/2001/XMLSchema#", "xsd:");
		URI_TO_PREFIX.put("http://www.w3.org/2000/01/rdf-schema#", "rdfs:");
		URI_TO_PREFIX.put("http://dbpedia.org/property/", "dbpedia2:");
	}

	/**
	 * Shorten the uri by applying prefixes.
	 */
	public static String shorten(String uri) {
		for (Entry<String, String> e : URI_TO_PREFIX.entrySet())
			if (uri.contains(e.getKey()))
				// there should be at most one replacement
				return uri.replace(e.getKey(), e.getValue());
		return uri;
	}

	/**
	 * Unshorten the uri by replacing prefixes with their full equivalent.
	 */
	public static String unshorten(String uri) {
		for (Entry<String, String> e : URI_TO_PREFIX.entrySet())
			if (uri.contains(e.getValue()))
				// there should be at most one replacement
				return uri.replace(e.getValue(), e.getKey());
		return uri;
	}

	/**
	 * Decode the provided URI assuming UTF-8 encoding.
	 */
	public static String decode(String uri) {
		try {
			return URLDecoder.decode(uri, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// this really shouldn't happen since UTF-8 is a valid encoding
			throw new IllegalStateException(e);
		}
	}

}
