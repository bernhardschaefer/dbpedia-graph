package de.unima.dws.dbpediagraph.subgraph.filter;

import java.util.List;

import org.apache.commons.configuration.Configuration;

import com.google.common.base.Predicate;
import com.tinkerpop.blueprints.Edge;

import de.unima.dws.dbpediagraph.util.*;

/**
 * Factory for retrieving {@link Predicate}s for {@link Edge}s.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class EdgePredicateFactory {
	public static final Predicate<Edge> DUMMY_PREDICATE = new Predicate<Edge>() {
		@Override
		public boolean apply(Edge e) {
			return true;
			// return e.getLabel().equals(GraphConfig.EDGE_LABEL);
		}
	};

	enum EdgeFilterType implements PredicateMapping<Edge> {
		DUMMY {
			@Override
			public Predicate<Edge> getPredicate() {
				return DUMMY_PREDICATE;
			}
		},
		CATEGORIES {
			@Override
			public Predicate<Edge> getPredicate() {
				return new CategoriesEdgePredicate();
			}
		},
		ONTOLOGY {
			@Override
			public Predicate<Edge> getPredicate() {
				return new OntologyEdgePredicate();
			}
		};
	}

	/**
	 * Get a {@link Predicate} implementation from config.
	 * 
	 * @param config
	 *            A configuration object where the {@link EdgeFilterType} is looked up.
	 * @return A {@link Predicate} instance.
	 */
	public static Predicate<Edge> fromConfig(final Configuration config, String configKey) {
		List<EdgeFilterType> edgeFilterTypes = EnumUtils.enumsfromConfig(EdgeFilterType.class, config, configKey);

		if (edgeFilterTypes == null || edgeFilterTypes.isEmpty()) // no edge filter requested
			return DUMMY_PREDICATE; // return dummy filter to use all triples

		return PredicateMappings.toAndPredicate(edgeFilterTypes);
	}
}
