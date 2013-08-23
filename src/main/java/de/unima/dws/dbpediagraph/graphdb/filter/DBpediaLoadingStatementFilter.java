package de.unima.dws.dbpediagraph.graphdb.filter;

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
public class DBpediaLoadingStatementFilter implements LoadingStatementFilter {

	@Override
	public boolean isValidStatement(Statement st) {
		// continue if object is literal
		if (st.getObject() instanceof Literal) {
			return false;
		}

		// continue if object is not a dbpedia resource
		// TODO check if this constraint is too large (e.g. dbpedia.org/ontology
		// is excluded!)
		/* 
		 * There are several possibilities here:
		 * 1. use only triples with object http://dbpedia.org/resource/*
		 * 2. use only triples with object http://dbpedia.org/*
		 * 3. use only triples with object that match various uris 
		 * 	( e.g. http://w3.org* | http://xmlns.com/foaf/0.1/Person | ...)
		 */
		if (!st.getObject().stringValue().startsWith(GraphConfig.DBPEDIA_RESOURCE_URI)) {
			return false;
		}

		return true;
	}

}
