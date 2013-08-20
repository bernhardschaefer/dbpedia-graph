package de.unima.dws.dbpediagraph.graphdb.loader;

import org.openrdf.model.Statement;

public interface StatementFilter {

	boolean isValidStatement(Statement st);

}
