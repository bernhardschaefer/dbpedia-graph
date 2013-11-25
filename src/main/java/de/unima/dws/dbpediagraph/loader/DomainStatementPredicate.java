package de.unima.dws.dbpediagraph.loader;

import org.openrdf.model.Literal;
import org.openrdf.model.Statement;

import com.google.common.base.Predicate;

/**
 * Filter that yields all statement with a DBpedia URI as object (http://dbpedia.org/*). This implementation is somewhat
 * inconsistent since it partly includes the class hierarchy.
 * 
 * @author Bernhard Schäfer
 * 
 */
class DomainStatementPredicate implements Predicate<Statement> {

	@Override
	public boolean apply(Statement st) {
		// continue if object is literal
		if (st.getObject() instanceof Literal)
			return false;

		if (!st.getObject().stringValue().startsWith("http://dbpedia.org/"))
			return false;

		return true;
	}

}
