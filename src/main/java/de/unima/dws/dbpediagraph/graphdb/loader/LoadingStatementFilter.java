package de.unima.dws.dbpediagraph.graphdb.loader;

import org.openrdf.model.Statement;

/**
 * RDF Statement (Triple) filter functionalities to determine whether statements are valid.
 * 
 * @author Bernhard Schäfer
 * 
 */
// TODO maybe change to guava Predicate<Statement> and then use e.g. by Iterables.filter(), Iterables.removeIf()
public interface LoadingStatementFilter {

	/**
	 * Predicate that decides if a statement is valid or not.
	 */
	boolean isValidStatement(Statement st);

}
