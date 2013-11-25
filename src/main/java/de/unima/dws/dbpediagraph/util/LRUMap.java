package de.unima.dws.dbpediagraph.util;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.collect.ForwardingMap;

/**
 * 
 * A simple Least Recently Used cache in Java. Note that this implementation is not synchronized.
 * 
 * @author ponzetto
 * @author Bernhard Schäfer
 * 
 */
public class LRUMap<K, V> extends ForwardingMap<K, V> implements Map<K, V> {

	private static final int DEFAULT_MAX_SIZE = 50;

	private final Map<K, V> cache;

	public LRUMap() {
		this(DEFAULT_MAX_SIZE);
	}

	public LRUMap(final int maxSize) {
		cache = new LinkedHashMap<K, V>(maxSize, .75F, true) {
			private static final long serialVersionUID = 2261365681291752981L;

			@Override
			protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
				return size() > maxSize;
			}
		};
	}

	@Override
	protected Map<K, V> delegate() {
		return cache;
	}

}