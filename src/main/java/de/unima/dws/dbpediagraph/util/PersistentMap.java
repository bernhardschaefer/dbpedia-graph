package de.unima.dws.dbpediagraph.util;

import java.util.Map;

/**
 * Persistent map which extends the {@link Map} interface with a {@link #close()} method.
 * 
 * @author Bernhard Schäfer
 */
public interface PersistentMap<K, V> extends Map<K, V> {
	void close();
}
