package de.unima.dws.dbpediagraph.weights;

import java.util.*;

/**
 * @author Bernhard Schäfer
 */
public class DummyOccurrenceCounts {
	public static final Map<String,Integer> DUMMY_MAP = new RandomDummyMap();
	
	private static class RandomDummyMap implements Map<String,Integer> {
		private static final Random RANDOM = new Random();
		
		@Override
		public int size() {
			return 30_000_000;
		}
		@Override
		public boolean isEmpty() {
			return false;
		}
		@Override
		public boolean containsKey(Object key) {
			return true;
		}
		@Override
		public boolean containsValue(Object value) {
			return true;
		}
		@Override
		public Integer get(Object key) {
			return RANDOM.nextInt(10_000);
		}

		@Override
		public Integer put(String key, Integer value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Integer remove(Object key) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void putAll(Map<? extends String, ? extends Integer> m) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Set<String> keySet() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Collection<Integer> values() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Set<java.util.Map.Entry<String, Integer>> entrySet() {
			throw new UnsupportedOperationException();
		}
		
	}
}
