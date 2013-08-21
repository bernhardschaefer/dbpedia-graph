package de.unima.dws.dbpediagraph.graphdb;

import java.util.Map.Entry;

import com.tinkerpop.blueprints.Graph;

/**
 * Implementation for shortening and unshortening uris using prefixes to reduce
 * the persisted size of the {@link Graph}.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class UriShortener {

	/**
	 * Shorten the uri by applying prefixes.
	 */
	public static String shorten(String uri) {
		for (Entry<String, String> e : GraphConfig.URI_TO_PREFIX.entrySet()) {
			if (uri.contains(e.getKey())) {
				// there should be at most one replacement
				return uri.replace(e.getKey(), e.getValue());
			}
		}
		return uri;
	}

	/**
	 * Unshorten the uri by replacing prefixes with their full equivalent.
	 */
	public static String unshorten(String uri) {
		for (Entry<String, String> e : GraphConfig.URI_TO_PREFIX.entrySet()) {
			if (uri.contains(e.getValue())) {
				// there should be at most one replacement
				return uri.replace(e.getValue(), e.getKey());
			}
		}
		return uri;
	}

}
