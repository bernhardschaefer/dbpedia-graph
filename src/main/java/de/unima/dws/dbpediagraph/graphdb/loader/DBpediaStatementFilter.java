package de.unima.dws.dbpediagraph.graphdb.loader;

import org.openrdf.model.Literal;
import org.openrdf.model.Statement;

import de.unima.dws.dbpediagraph.graphdb.GraphConfig;

/**
 * In the context of DBpedia a statement is valid if it is related to two
 * resources and thus useful for graph-based disambiguation.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class DBpediaStatementFilter implements StatementFilter {

	@Override
	public boolean isValidStatement(Statement st) {
		// continue if object is literal
		if (st.getObject() instanceof Literal) {
			return false;
		}

		// continue if object is not a dbpedia resource
		if (!st.getObject().stringValue().startsWith(GraphConfig.DBPEDIA_RESOURCE_URI)) {
			return false;
		}

		return true;
	}

}
