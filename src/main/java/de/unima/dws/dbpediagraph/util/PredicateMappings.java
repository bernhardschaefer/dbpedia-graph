package de.unima.dws.dbpediagraph.util;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

public class PredicateMappings {
	public static <T> Predicate<T> toAndPredicate(List<? extends PredicateMapping<T>> types) {
		List<Predicate<T>> predicates = new ArrayList<>();
		for (PredicateMapping<T> type : types)
			predicates.add(type.getPredicate());
		return Predicates.and(predicates);
	}
}
