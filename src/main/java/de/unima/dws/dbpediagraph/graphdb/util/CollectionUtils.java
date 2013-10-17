package de.unima.dws.dbpediagraph.graphdb.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.tinkerpop.blueprints.Vertex;

public class CollectionUtils {
	public static <T> Collection<T> combine(Collection<Collection<T>> collections) {
		Collection<T> combinedCollections = new ArrayList<T>();
		for (Collection<T> c : collections) {
			combinedCollections.addAll(c);
		}
		return combinedCollections;
	}

	/**
	 * Remove all entries from collection a that are in collection b and return a new collection.
	 */
	public static <T> Collection<T> removeAll(Collection<T> a, Collection<T> b) {
		// TODO use hash set to improve performance since returned collection is mostly used with contains()
		Collection<T> c = new ArrayList<T>(a);
		c.removeAll(b);
		return c;
	}

	public static Collection<Collection<Vertex>> split(Collection<Vertex> senses) {
		Collection<Collection<Vertex>> vertices = new ArrayList<>();
		for (Vertex v : senses) {
			vertices.add(Arrays.asList(v));
		}
		return vertices;
	}
}
