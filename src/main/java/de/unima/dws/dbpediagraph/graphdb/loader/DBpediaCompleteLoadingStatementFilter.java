package de.unima.dws.dbpediagraph.graphdb.loader;

import org.openrdf.model.Literal;
import org.openrdf.model.Statement;

/**
 * Filter that yields all statement with DBpedia resources, ontologies, or yago classes as object. This includes the
 * entire class hierarchy and classes such as foaf:Person.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class DBpediaCompleteLoadingStatementFilter implements LoadingStatementFilter {

	/**
	 * valid uri prefixes from http://dbpedia.org/snorql/.
	 */
	// TODO check if compiled pattern with all prefixes has better performance
	private static final String[] uriPrefixes = new String[] { "http://dbpedia.org/", // dbpedia resource, property
			"http://www.w3.org/", // owl,xsd,rdfs,rdf,skos
			"http://xmlns.com/foaf", // foaf
			"http://purl.org/dc", // dc
	};

	@Override
	public boolean isValidStatement(Statement st) {
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
