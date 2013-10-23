package de.unima.dws.dbpediagraph.graphdb.loader;

import org.apache.commons.configuration.Configuration;

public class LoadingStatementFilterFactory {

	private static final String LOADING_STATEMENT_FILTER_KEY = "loading.filter";

	/**
	 * Get a {@link LoadingStatementFilter} implementation.
	 * 
	 * @param configuration
	 *            A configuration object where the class is looked up.
	 * @return A {@link LoadingStatementFilter} instance.
	 */
	public static LoadingStatementFilter getImpl(final Configuration configuration) {
		final String clazz = configuration.getString(LOADING_STATEMENT_FILTER_KEY, null);

		if (clazz == null) {
			throw new RuntimeException("Configuration must contain a valid '" + LOADING_STATEMENT_FILTER_KEY
					+ "' setting");
		}

		Class<?> filterClass;
		try {
			filterClass = Class.forName(clazz);
		} catch (ClassNotFoundException cnfe) {
			throw new RuntimeException(String.format(
					"[%s] could not find [%s].  Ensure that the jar is in the classpath.",
					LoadingStatementFilterFactory.class.getName(), clazz));
		}

		LoadingStatementFilter statementFilter = null;
		try {
			statementFilter = (LoadingStatementFilter) filterClass.newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | SecurityException e) {
			throw new RuntimeException("Creating a new instance failed", e);
		}
		return statementFilter;
	}
}
