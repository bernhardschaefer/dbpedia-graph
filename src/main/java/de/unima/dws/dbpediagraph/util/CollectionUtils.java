package de.unima.dws.dbpediagraph.util;

import java.util.*;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.disambiguate.local.HITSCentrality.HitsScores;
import de.unima.dws.dbpediagraph.graph.Graphs;

/**
 * Noninstantiable static collection utility class.
 * 
 * @author Bernhard Schäfer
 * 
 */
public final class CollectionUtils {

	public static <T> Set<T> combine(Collection<Set<T>> collections) {
		Set<T> combinedCollections = new HashSet<T>();
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

	public static <T> int iterableItemCount(Iterable<T> iterable) {
		return iterableItemCount(iterable, Integer.MAX_VALUE);
	}

	public static <T> int iterableItemCount(Iterable<T> iterable, int threshold) {
		if (iterable instanceof Collection)
			return ((Collection<T>) iterable).size();
		return iteratorItemCount(iterable.iterator(), threshold);
	}

	public static <T> Collection<T> iterableToCollection(Iterable<T> itty) {
		if (itty instanceof Collection)
			return (Collection<T>) itty;
		return Lists.newArrayList(itty);
	}

	private static int iteratorItemCount(Iterator<?> iter, int threshold) {
		int counter = 0;
		while (iter.hasNext()) {
			iter.next();
			if (counter++ >= threshold)
				return counter;
		}
		return counter;
	}

	public static <T> List<T> joinListValues(Map<?, List<T>> map) {
		List<T> joinedValues = new ArrayList<>();
		for (List<T> list : map.values()) {
			joinedValues.addAll(list);
		}
		return joinedValues;
	}

	public static <T> Set<T> joinSetValues(Map<?, Set<T>> map) {
		Set<T> joinedValues = new HashSet<>();
		for (Set<T> list : map.values()) {
			joinedValues.addAll(list);
		}
		return joinedValues;
	}

	/**
	 * Remove the value from collection coll and return a new collection.
	 */
	public static <T> Set<T> remove(Collection<T> coll, T value) {
		Set<T> copy = new HashSet<T>(coll);
		copy.remove(value);
		return copy;
	}

	/**
	 * Remove all entries from collection a that are in collection b and returns a new collection.
	 */
	public static <T> Set<T> removeAll(Collection<T> a, Collection<T> b) {
		Set<T> c = new HashSet<T>(a);
		c.removeAll(b);
		return c;
	}

	public static <T> Collection<Collection<T>> split(Collection<T> collection) {
		Collection<Collection<T>> splitted = new ArrayList<>();
		for (T entry : collection)
			splitted.add(Arrays.asList(entry));
		return splitted;
	}

	// prevent default constructor for noninstantiability
	private CollectionUtils() {
		throw new AssertionError();
	}

	public static int countCollectionValues(Map<?, ? extends Collection<?>> map) {
		int counter = 0;
		for (Collection<?> c : map.values()) {
			if (c != null)
				counter += c.size();
		}
		return counter;
	}

	public static Set<Vertex> findContainingCollection(Collection<Collection<Vertex>> surfaceFormVertices,
			Vertex searchVertex) {
		for (Collection<Vertex> vertices : surfaceFormVertices)
			for (Vertex v : vertices)
				// simple contains won't work since vertices can be from different graphs with different id
				// representations
				if (Graphs.uriOfVertex(v).equals(Graphs.uriOfVertex(searchVertex)))
					return new HashSet<>(vertices);
		throw new IllegalArgumentException("Vertex is not in one of the provided collections.");
	}
}
