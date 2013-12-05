package de.unima.dws.dbpediagraph.loader;

import com.google.common.base.Predicate;

/**
 * Filter that yields all statement with a DBpedia URI as object (http://dbpedia.org/*). This implementation is somewhat
 * inconsistent since it partly includes the class hierarchy.
 * 
 * @author Bernhard Schäfer
 * 
 */
class DomainStatementPredicate implements Predicate<Triple> {

	@Override
	public boolean apply(Triple t) {
		if (!t.object().startsWith("http://dbpedia.org/"))
			return false;
		return true;
	}

}
