package de.unima.dws.dbpediagraph.loader;

import com.google.common.base.Predicate;

import de.unima.dws.dbpediagraph.graph.GraphConfig;

/**
 * Filter that yields all statement with DBpedia resources as subject and object, which means the match the pattern
 * http://dbpedia.org/resource/* .
 * 
 * @author Bernhard Schäfer
 * 
 */
class ResourceStatementPredicate implements Predicate<Triple> {

	@Override
	public boolean apply(Triple t) {
		if (!t.object().startsWith(GraphConfig.DBPEDIA_RESOURCE_PREFIX))
			return false;
		return true;
	}

}
