package de.unima.dws.dbpediagraph.util;

import java.util.Map;

public interface PersistedMap<K, V> extends Map<K,V> {
	void close();
}
