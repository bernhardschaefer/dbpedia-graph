package de.unima.dws.dbpediagraph.graphdb.loader;

import org.openrdf.model.Statement;

/**
 * RDF Statement (Triple) filter functionalities to determine whether statements
 * are valid.
 * 
 * @author Bernhard Schäfer
 * 
 */
public interface StatementFilter {

	/**
	 * Predicate that decides if a statement is valid or not.
	 */
	boolean isValidStatement(Statement st);

}
