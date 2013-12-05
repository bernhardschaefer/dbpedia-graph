package de.unima.dws.dbpediagraph.loader;

import org.openrdf.model.Statement;

import de.unima.dws.dbpediagraph.graph.UriTransformer;

/**
 * Light RDF triple implementation which decodes subject, predicate, and object assuming UTF-8 representation.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class Triple {
	private final String subject;
	private final String predicate;
	private final String object;

	public Triple(String subject, String predicate, String object) {
		this.subject = UriTransformer.decode(subject);
		this.predicate = UriTransformer.decode(predicate);
		this.object = UriTransformer.decode(object);
	}

	public String subject() {
		return subject;
	}

	public String predicate() {
		return predicate;
	}

	public String object() {
		return object;
	}

	public static Triple fromStatement(Statement st) {
		return new Triple(st.getSubject().stringValue(), st.getPredicate().stringValue(), st.getObject().stringValue());
	}

}
