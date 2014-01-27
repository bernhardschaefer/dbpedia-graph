package de.unima.dws.dbpediagraph.loader;

import java.util.List;

import org.apache.commons.configuration.Configuration;

import com.google.common.base.Predicate;

import de.unima.dws.dbpediagraph.graph.GraphConfig;
import de.unima.dws.dbpediagraph.util.*;

/**
 * Factory for retrieving {@link Predicate}s for {@link Triple}s.
 * 
 * @author Bernhard Schäfer
 * 
 */
class TriplePredicateFactory {

	private static final String CONFIG_TRIPLE_PREDICATE = "loading.filter.impl";
	private static final Predicate<Triple> DUMMY_PREDICATE = new Predicate<Triple>() {
		@Override
		public boolean apply(Triple t) {
			return true;
		}
	};

	enum LoadingType implements PredicateMapping<Triple>  {
		BLACKLIST {
			@Override
			public Predicate<Triple> getPredicate() {
				return new BlacklistTriplePredicate(GraphConfig.config());
			}
		}, COMPLETE {
			@Override
			public Predicate<Triple> getPredicate() {
				return new CompleteTriplePredicate();
			}
		}, DOMAIN {
			@Override
			public Predicate<Triple> getPredicate() {
				return new DomainTriplePredicate();
			}
		}, RESOURCE {
			@Override
			public Predicate<Triple> getPredicate() {
				return new ResourceTriplePredicate();
			}
		};

	}


	/**
	 * Get a {@link Predicate} implementation from config.
	 * 
	 * @param config
	 *            A configuration object where the {@link LoadingType} is looked up.
	 * @return A {@link Predicate} instance.
	 */
	static Predicate<Triple> fromConfig(final Configuration config) {
		List<LoadingType> loadingTypes = EnumUtils.enumsfromConfig(LoadingType.class, config, CONFIG_TRIPLE_PREDICATE);

		if (loadingTypes == null || loadingTypes.isEmpty()) // no loading filter requested
			return DUMMY_PREDICATE; // return dummy filter to use all triples

		return PredicateMappings.toAndPredicate(loadingTypes);
	}

}
