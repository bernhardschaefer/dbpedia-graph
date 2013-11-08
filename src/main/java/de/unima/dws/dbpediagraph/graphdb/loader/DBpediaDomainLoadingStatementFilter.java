package de.unima.dws.dbpediagraph.graphdb.loader;

import org.openrdf.model.Literal;
import org.openrdf.model.Statement;

/**
 * Filter that yields all statement with a DBpedia URI as object (http://dbpedia.org/*). This implementation is somewhat
 * inconsistent since it partly includes the class hierarchy.
 * 
 * @author Bernhard Schäfer
 * 
 */
class DBpediaDomainLoadingStatementFilter implements LoadingStatementFilter {

	@Override
	public boolean isValidStatement(Statement st) {
		// continue if object is literal
		if (st.getObject() instanceof Literal)
			return false;

		if (!st.getObject().stringValue().startsWith("http://dbpedia.org/"))
			return false;

		return true;
	}

}
