package de.unima.dws.dbpediagraph.loader;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.openrdf.model.Statement;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import de.unima.dws.dbpediagraph.graph.GraphConfig;

/**
 * Factory for retrieving {@link Predicate}s for {@link Statement}s.
 * 
 * @author Bernhard Schäfer
 * 
 */
class StatementPredicateFactory {

	private static final String CONFIG_STATEMENT_PREDICATE = "loading.filter.impl";
	private static final Predicate<Statement> DUMMY_PREDICATE = new Predicate<Statement>() {
		@Override
		public boolean apply(Statement input) {
			return true;
		}
	};

	enum LoadingType {
		BLACKLIST, COMPLETE, DOMAIN, RESOURCE;

		static List<LoadingType> fromConfig(Configuration config) {
			List<LoadingType> loadingTypes = new ArrayList<>();
			@SuppressWarnings("unchecked")
			// apache commons config does not support generics
			List<String> loadingTypeNames = config.getList(CONFIG_STATEMENT_PREDICATE);

			for (String loadingTypeName : loadingTypeNames) {
				try {
					loadingTypes.add(LoadingType.valueOf(loadingTypeName.trim()));
				} catch (IllegalArgumentException e) {
					throw new IllegalArgumentException(String.format(
							"Unknown loading filter type '%s' specified in '%s'. Only the following are allowed: %s",
							loadingTypeName, CONFIG_STATEMENT_PREDICATE,
							java.util.Arrays.toString(LoadingType.values())), e);
				}
			}
			return loadingTypes;
		}
	}

	static Predicate<Statement> fromLoadingTypes(List<LoadingType> types) {
		List<Predicate<Statement>> predicates = new ArrayList<>();
		for (LoadingType type : types)
			predicates.add(fromLoadingType(type));
		return Predicates.and(predicates);
	}

	static Predicate<Statement> fromLoadingType(LoadingType type) {
		switch (type) {
		case BLACKLIST:
			return new BlacklistStatementPredicate(GraphConfig.config());
		case COMPLETE:
			return new CompleteStatementPredicate();
		case DOMAIN:
			return new DomainStatementPredicate();
		case RESOURCE:
			return new ResourceStatementPredicate();
		}
		throw new IllegalArgumentException("The provided " + LoadingType.class.getSimpleName() + " is not valid: "
				+ type);
	}

	/**
	 * Get a {@link Predicate} implementation from config.
	 * 
	 * @param config
	 *            A configuration object where the {@link LoadingType} is looked up.
	 * @return A {@link Predicate} instance.
	 */
	static Predicate<Statement> fromConfig(final Configuration config) {
		List<LoadingType> loadingTypes = LoadingType.fromConfig(config);

		if (loadingTypes == null || loadingTypes.isEmpty()) // no loading filter requested
			return DUMMY_PREDICATE; // return dummy filter to use all triples

		return fromLoadingTypes(loadingTypes);
	}

}
