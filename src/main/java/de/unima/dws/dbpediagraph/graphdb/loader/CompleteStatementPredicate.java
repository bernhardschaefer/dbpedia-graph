package de.unima.dws.dbpediagraph.graphdb.loader;

import org.openrdf.model.Literal;
import org.openrdf.model.Statement;

import com.google.common.base.Predicate;

/**
 * Filter that yields all statement with DBpedia resources, ontologies, or yago classes as object. This includes the
 * entire class hierarchy and classes such as foaf:Person.
 * 
 * @author Bernhard Schäfer
 * 
 */
class CompleteStatementPredicate implements Predicate<Statement> {

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
	public boolean apply(Statement st) {
		// continue if object is literal
		if (st.getObject() instanceof Literal)
			return false;

		String objectUri = st.getObject().stringValue();
		for (String uriPrefix : uriPrefixes)
			if (objectUri.startsWith(uriPrefix))
				return true;
		return false;
	}

}
