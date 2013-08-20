package de.unima.dws.dbpediagraph.graphdb.loader;

import org.openrdf.model.Literal;
import org.openrdf.model.Statement;

import de.unima.dws.dbpediagraph.graphdb.GraphConfig;

public class DBpediaStatementFilter implements StatementFilter {

	@Override
	public boolean isValidStatement(Statement st) {
		// continue if object is literal
		if (st.getObject() instanceof Literal) {
			// literalObjTriples++;
			return false;
		}

		// continue if object is not a dbpedia resource
		// TODO check if prefix should contain dbepdia.org/resource
		if (!st.getObject().stringValue().startsWith(GraphConfig.DBPEDIA_RESOURCE_URI)) {
			// noDBpediaObjTriples++;
			return false;
		}

		return true;
	}

}
