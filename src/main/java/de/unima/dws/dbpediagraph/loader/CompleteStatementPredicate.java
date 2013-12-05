package de.unima.dws.dbpediagraph.loader;

import com.google.common.base.Predicate;

/**
 * Filter that yields all statement with DBpedia resources, ontologies, or yago classes as object. This includes the
 * entire class hierarchy and classes such as foaf:Person.
 * 
 * @author Bernhard Schäfer
 * 
 */
class CompleteStatementPredicate implements Predicate<Triple> {

	/**
	 * valid uri prefixes from http://dbpedia.org/snorql/.
	 */
	// TODO(if needed) check if compiled pattern with all prefixes has better performance
	private static final String[] uriPrefixes = new String[] { "http://dbpedia.org/", // dbpedia resource, property
			"http://www.w3.org/", // owl,xsd,rdfs,rdf,skos
			"http://xmlns.com/foaf", // foaf
			"http://purl.org/dc", // dc
	};

	@Override
	public boolean apply(Triple t) {
		for (String uriPrefix : uriPrefixes)
			if (t.object().startsWith(uriPrefix))
				return true;
		return false;
	}

}
