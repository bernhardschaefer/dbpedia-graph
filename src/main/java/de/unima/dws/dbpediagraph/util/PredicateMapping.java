package de.unima.dws.dbpediagraph.util;

import com.google.common.base.Predicate;

public interface PredicateMapping<T> {
	public Predicate<T> getPredicate();
}
