package de.unima.dws.dbpediagraph.graphdb.util;

import java.util.*;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.disambiguate.local.HITSCentrality.HitsScores;

/**
 * Noninstantiable static collection utility class.
 * 
 * @author Bernhard Schäfer
 * 
 */
public final class CollectionUtils {

	/**
	 * Return the best k entries from the collection according to the provided comparator.
	 */
	public static <T> List<T> bestK(Collection<T> collection, int k, Comparator<T> comparator) {
		List<T> sortedResources = new ArrayList<>(collection);
		Collections.sort(sortedResources, comparator);
		collection = sortedResources.subList(0, k);
		return sortedResources;
	}

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

	/**
	 * Remove all entries from collection a that are in collection b and return a new collection.
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
}
