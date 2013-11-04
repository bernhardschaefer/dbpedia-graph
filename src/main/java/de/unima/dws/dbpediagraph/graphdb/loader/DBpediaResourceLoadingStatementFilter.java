package de.unima.dws.dbpediagraph.graphdb.loader;

import org.openrdf.model.Literal;
import org.openrdf.model.Statement;

import de.unima.dws.dbpediagraph.graphdb.GraphConfig;

/**
 * Filter that yields all statement with DBpedia resources as subject and object, which means the match the pattern
 * http://dbpedia.org/resource/* .
 * 
 * @author Bernhard Schäfer
 * 
 */
class DBpediaResourceLoadingStatementFilter implements LoadingStatementFilter {

	@Override
	public boolean isValidStatement(Statement st) {
		// continue if object is literal
		if (st.getObject() instanceof Literal)
			return false;

		if (!st.getObject().stringValue().startsWith(GraphConfig.DBPEDIA_RESOURCE_PREFIX))
			return false;

		return true;
	}

}
