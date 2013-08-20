package de.unima.dws.dbpediagraph.graphdb;

import java.util.Map.Entry;

public class UriShortener {

	public static String shorten(String uri) {
		for (Entry<String, String> e : GraphConfig.URI_TO_PREFIX.entrySet()) {
			if (uri.contains(e.getKey())) {
				// there should be at most one replacement
				return uri.replace(e.getKey(), e.getValue());
			}
		}
		return uri;
	}

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
