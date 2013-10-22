package de.unima.dws.dbpediagraph.graphdb.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Lists;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.disambiguate.local.HITSCentrality.HitsScores;

public class CollectionUtils {
	public static <T> Collection<T> combine(Collection<Collection<T>> collections) {
		Collection<T> combinedCollections = new ArrayList<T>();
		for (Collection<T> c : collections)
			combinedCollections.addAll(c);
		return combinedCollections;
	}

	public static Map<Vertex, HitsScores> deepCopy(Map<Vertex, HitsScores> scores) {
		Map<Vertex, HitsScores> copy = new HashMap<>(scores.size());
		for (Entry<Vertex, HitsScores> entry : scores.entrySet())
			copy.put(entry.getKey(), new HitsScores(entry.getValue()));
		return copy;
	}

	public static int getIterItemCount(Iterator<?> iter) {
		int counter = 0;
		while (iter.hasNext()) {
			iter.next();
			counter++;
		}
		return counter;
	}

	public static <T> Collection<T> iterToCollection(Iterable<T> itty) {
		if (itty instanceof Collection)
			return (Collection<T>) itty;
		return Lists.newArrayList(itty);
	}

	/**
	 * Remove all entries from collection a that are in collection b and return a new collection.
	 */
	public static <T> Set<T> removeAll(Collection<T> a, Collection<T> b) {
		Set<T> c = new HashSet<T>(a);
		c.removeAll(b);
		return c;
	}

	public static Collection<Collection<Vertex>> split(Collection<Vertex> senses) {
		Collection<Collection<Vertex>> vertices = new ArrayList<>();
		for (Vertex v : senses)
			vertices.add(Arrays.asList(v));
		return vertices;
	}
}
